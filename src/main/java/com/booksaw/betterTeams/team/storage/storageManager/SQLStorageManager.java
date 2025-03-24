package com.booksaw.betterTeams.team.storage.storageManager;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.database.BetterTeamsDatabase;
import com.booksaw.betterTeams.database.TableName;
import com.booksaw.betterTeams.team.LocationSetComponent;
import com.booksaw.betterTeams.team.TeamManager;
import com.booksaw.betterTeams.team.storage.team.SQLTeamStorage;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;
import lombok.Getter;
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

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import static com.booksaw.betterTeams.util.StringUtil.EMPTY_STRING_ARRAY;

public class SQLStorageManager extends TeamManager implements Listener {

	@Getter
	private BetterTeamsDatabase database;

	protected final FileConfiguration teamStorage;

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

	private HashMap<String, UUID> claims;

	@Override
	public UUID getClaimingTeamUUID(Location location) {

		if (claims == null) {
			claims = new HashMap<>();
			try (PreparedStatement preparedStatement = database.select("*", TableName.CHESTCLAIMS)) {
				ResultSet results = preparedStatement.executeQuery();
				while (results.next()) {
					claims.put(results.getString(2), UUID.fromString(results.getString(1)));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}

		return claims.get(LocationSetComponent.getString(location));
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
				"'" + player.getPlayer().getUniqueId() + "', '" + team.getID() + "', " + player.getRank().value);
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

		PreparedStatement ps = database.selectOrder("name", TableName.TEAM, "SCORE DESC");

		return getTeamsFromResultSet(ps);
	}

	@Override
	public String[] sortTeamsByBalance() {
		PreparedStatement ps = database.selectOrder("name", TableName.TEAM, "money DESC");

		return getTeamsFromResultSet(ps);
	}

	@Override
	public String[] sortTeamsByMembers() {
		PreparedStatement ps = database.selectInnerJoinGroupByOrder("name, COUNT(" + TableName.PLAYERS + ".playerUUID)",
				TableName.TEAM, TableName.PLAYERS, TableName.TEAM + ".teamID = " + TableName.PLAYERS + ".teamID",
				"name", "COUNT(" + TableName.PLAYERS + ".playerUUID) DESC");

		return getTeamsFromResultSet(ps);
	}

	/**
	 * convert a result set, supplied by a prepared statement into a list of teams
	 * for sort methods
	 *
	 * @param ps the statement
	 * @return the ordered team list or an empty array in the event of an error
	 */
	private String[] getTeamsFromResultSet(PreparedStatement ps) {

		try {
			ResultSet results = ps.executeQuery();
			results.first();

			List<String> toReturn = new ArrayList<>();

			try {
				toReturn.add(results.getString("name"));
			} catch (SQLException e) {
				// called when no teams have been created
				return EMPTY_STRING_ARRAY;
			}

			while (results.next()) {
				toReturn.add(results.getString("name"));
			}
			ps.close();
			return toReturn.toArray(EMPTY_STRING_ARRAY);

		} catch (Exception e) {
			Main.plugin.getLogger().severe("Could not sort teams for results, report the following error:");
			e.printStackTrace();
			return EMPTY_STRING_ARRAY;
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

	@Override
	public void purgeTeamMoney() {
		// reseting the loaded teams so the results are immediate
		for (Entry<UUID, Team> team : loadedTeams.entrySet()) {
			team.getValue().setMoney(0);
		}

		database.updateRecord(TableName.TEAM, "money = 0");

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
			Main.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
		}
	}

	@Override
	public void addChestClaim(Team team, Location loc) {
		claims.put(LocationSetComponent.getString(loc), team.getID());
		database.insertRecord(TableName.CHESTCLAIMS, "teamID, chestLoc",
				"'" + team.getID() + "', '" + LocationSetComponent.getString(loc) + "'");
	}

	@Override
	public void removeChestclaim(Location loc) {
		claims.remove(LocationSetComponent.getString(loc));
		database.deleteRecord(TableName.CHESTCLAIMS, "chestLoc LIKE '" + LocationSetComponent.getString(loc) + "'");
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

		if (team.getOnlineMembers().size() > 1) {
			team.getTeamPlayer(e.getPlayer()).setTeamChat(false);
			return;
		}

		unloadTeam(teamUUID);

	}

}
