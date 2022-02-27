package com.booksaw.betterTeams.team.storage.storageManager;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.database.BetterTeamsDatabase;
import com.booksaw.betterTeams.database.TableName;
import com.booksaw.betterTeams.team.LocationListComponent;
import com.booksaw.betterTeams.team.TeamManager;
import com.booksaw.betterTeams.team.storage.team.SQLTeamStorage;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class SQLStorageManager extends TeamManager implements Listener {

	private BetterTeamsDatabase database;

	protected FileConfiguration teamStorage;

	public static final String TEAMLISTSTORAGELOC = "teams";

	public SQLStorageManager() {
		setupDatabaseConnection();

		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);

		// loading the teamStorage variable
		File f = new File("plugins/BetterTeams/" + TEAMLISTSTORAGELOC + ".yml");

		if (!f.exists()) {
			Main.plugin.saveResource("teams.yml", false);
		}

		teamStorage = YamlConfiguration.loadConfiguration(f);
	}

	private void setupDatabaseConnection() {
		database = new BetterTeamsDatabase();
		database.setupConnectionFromConfiguration(Main.plugin.getConfig().getConfigurationSection("database"));
		database.setupTables();
	}

	@Override
	public void disable() {
		database.closeConnection();
	}

	@Override
	public UUID getClaimingTeamUUID(Location location) {
		String result = database.getResult("teamID", TableName.CHESTCLAIMS,
				"chestLoc LIKE '" + LocationListComponent.getString(location) + "'");
		if (result == null || result.length() == 0) {
			return null;
		}
		return UUID.fromString(result);
	}

	@Override
	public boolean isTeam(UUID uuid) {
		return database.hasResult(TableName.TEAM, "teamID LIKE '" + uuid.toString() + "'");
	}

	@Override
	public boolean isTeam(String name) {
		return database.hasResult(TableName.TEAM, "UPPER(name) LIKE '" + name.toUpperCase() + "'");
	}

	@Override
	public boolean isInTeam(OfflinePlayer player) {
		return database.hasResult(TableName.PLAYERS, "playerUUID LIKE '" + player.getUniqueId() + "'");
	}

	@Override
	public UUID getTeamUUID(OfflinePlayer player) {
		return UUID.fromString(
				database.getResult("teamID", TableName.PLAYERS, "playerUUID LIKE '" + player.getUniqueId() + "'"));
	}

	@Override
	public UUID getTeamUUID(String name) {
		return UUID.fromString(
				database.getResult("teamID", TableName.TEAM, "UPPER(name) LIKE '" + name.toUpperCase() + "'"));
	}

	@Override
	public void loadTeams() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (isInTeam(player)) {
				loadTeam(getTeamUUID(player));
			}
		}
	}

	public void loadTeam(UUID uuid) {
		if (uuid == null || isLoaded(uuid)) {
			return;
		}

		Team team;

		try {
			team = new Team(uuid);
		} catch (IllegalArgumentException e) {
			return;
		}

		loadedTeams.put(uuid, team);

	}

	public void unloadTeam(UUID uuid) {
		Team team = Team.getTeam(uuid);
		if (team.getScoreboardTeamOrNull() != null) {
			team.getScoreboardTeamOrNull().unregister();
		}
		loadedTeams.remove(uuid);
	}

	@Override
	protected void registerNewTeam(Team team, Player player) {
		// no lookup tables so this method is not required
	}

	@Override
	protected void deleteTeamStorage(Team team) {
		database.deleteRecord(TableName.TEAM, "teamID LIKE '" + team.getID() + "'");
	}

	@Override
	public void teamNameChange(Team team, String newName) {
		database.updateRecordWhere(TableName.TEAM, "name = '" + newName + "'", "teamID LIKE '" + team.getID() + "'");
	}

	@Override
	public void playerJoinTeam(Team team, TeamPlayer player) {
		database.insertRecord(TableName.PLAYERS, "playerUUID, teamID, playerRank",
				"'" + player.getPlayer().getUniqueId() + "', '" + team.getID() + "', " + player.getRank().value + "");
	}

	@Override
	public void playerLeaveTeam(Team team, TeamPlayer player) {
		database.deleteRecord(TableName.PLAYERS, "playerUUID LIKE '" + player.getPlayer().getUniqueId() + "'");
	}

	@Override
	public TeamStorage createTeamStorage(Team team) {
		return new SQLTeamStorage(this, team);
	}

	@Override
	public TeamStorage createNewTeamStorage(Team team) {
		database.insertRecord(TableName.TEAM, "teamID, name", "'" + team.getID() + "', '" + team.getName() + "'");
		return new SQLTeamStorage(this, team);
	}

	@Override
	public String[] sortTeamsByScore() {

		ResultSet results = database.selectOrder("name", TableName.TEAM, "SCORE DESC");

		return getTeamsFromResultSet(results);
	}

	@Override
	public String[] sortTeamsByBalance() {
		ResultSet results = database.selectOrder("name", TableName.TEAM, "money DESC");

		return getTeamsFromResultSet(results);
	}

	@Override
	public String[] sortTeamsByMembers() {
		ResultSet results = database.selectInnerJoinGroupByOrder("name, COUNT(" + TableName.PLAYERS + ".playerUUID)",
				TableName.TEAM, TableName.PLAYERS, TableName.TEAM + ".teamID = " + TableName.PLAYERS + ".teamID",
				"name", "COUNT(" + TableName.PLAYERS + ".playerUUID) DESC");

		return getTeamsFromResultSet(results);
	}

	/**
	 * convert a result set into a list of teams for sort methods
	 * 
	 * @param results
	 * @return the ordered team list or an empty array in the event of an error
	 */
	private String[] getTeamsFromResultSet(ResultSet results) {

		try {
			results.first();

			List<String> toReturn = new ArrayList<>();

			try {
				toReturn.add(results.getString("name"));
			} catch (SQLException e) {
				// called when no teams have been created
				return new String[0];
			}

			while (results.next()) {
				toReturn.add(results.getString("name"));
			}

			return toReturn.toArray(new String[toReturn.size()]);

		} catch (Exception e) {
			Bukkit.getLogger().severe("Could not sort teams for results, report the following error:");
			e.printStackTrace();
			return new String[0];
		}

	}

	@Override
	public void purgeTeamScore() {
		// reseting the loaded teams so the results are immediate
		for (Entry<UUID, Team> team : loadedTeams.entrySet()) {
			team.getValue().setScore(0);
		}

		database.updateRecord(TableName.TEAM, "score = 0");

	}

	private static final String HOLOPATH = "holos";

	@Override
	public List<String> getHoloDetails() {
		return teamStorage.getStringList(HOLOPATH);
	}

	@Override
	public void setHoloDetails(List<String> details) {
		teamStorage.set(HOLOPATH, details);
		saveTeamsFile();
	}

	public void saveTeamsFile() {
		File f = new File("plugins/BetterTeams/" + TEAMLISTSTORAGELOC + ".yml");
		try {
			teamStorage.save(f);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
		}
	}

	@Override
	public void addChestClaim(Team team, Location loc) {
		database.insertRecord(TableName.CHESTCLAIMS, "teamID, chestLoc",
				"'" + team.getID() + "', '" + LocationListComponent.getString(loc) + "'");
	}

	@Override
	public void removeChestclaim(Location loc) {
		database.deleteRecord(TableName.CHESTCLAIMS, "chestLoc LIKE '" + LocationListComponent.getString(loc) + "'");
	}

	@Override
	public void rebuildLookups() {
		// not required
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!isInTeam(e.getPlayer())) {
			return;
		}

		loadTeam(getTeamUUID(e.getPlayer()));
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (!isInTeam(e.getPlayer())) {
			return;
		}
		UUID teamUUID = getTeamUUID(e.getPlayer());

		Team team = getTeam(teamUUID);

		if (team.getOnlineMemebers().size() > 1) {
			team.getTeamPlayer(e.getPlayer()).setTeamChat(false);
			return;
		}

		unloadTeam(teamUUID);

	}

	public BetterTeamsDatabase getDatabase() {
		return database;
	}

}
