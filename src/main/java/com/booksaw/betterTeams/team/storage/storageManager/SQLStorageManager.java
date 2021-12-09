package com.booksaw.betterTeams.team.storage.storageManager;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
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
import com.booksaw.betterTeams.team.TeamManager;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class SQLStorageManager extends TeamManager implements Listener {

	BetterTeamsDatabase database;

	public SQLStorageManager() {
		setupDatabaseConnection();

		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
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
		return UUID.fromString(database.getResult("teamID", TableName.CHESTCLAIMS, "chestLoc == " + location));
	}

	@Override
	public boolean isTeam(UUID uuid) {
		return database.hasResult(TableName.TEAM, "teamID == " + uuid.toString());
	}

	@Override
	public boolean isTeam(String name) {
		return database.hasResult(TableName.TEAM, "UPPER(name) == " + name.toUpperCase());
	}

	@Override
	public boolean isInTeam(OfflinePlayer player) {
		return database.hasResult(TableName.PLAYERS, "playerUUID == " + player.getUniqueId());
	}

	@Override
	public UUID getTeamUUID(OfflinePlayer player) {
		return UUID
				.fromString(database.getResult("teamID", TableName.PLAYERS, "playerUUID == " + player.getUniqueId()));
	}

	@Override
	public UUID getTeamUUID(String name) {
		return UUID.fromString(database.getResult("teamID", TableName.TEAM, "UPPER(name) == " + name.toUpperCase()));
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
		loadedTeams.remove(uuid);
	}

	@Override
	protected void registerNewTeam(Team team, Player player) {
		// no lookup tables so this method is not required
	}

	@Override
	protected void deleteTeamStorage(Team team) {
		database.deleteRecord(TableName.TEAM, "teamID == " + team.getID());
	}

	@Override
	public void teamNameChange(Team team, String newName) {
		database.updateRecord(TableName.TEAM, "name", "teamID == " + team.getID());
	}

	@Override
	public void playerJoinTeam(Team team, TeamPlayer player) {
		database.insertRecord(TableName.PLAYERS, "playerUUID, teamID, playerRank",
				player.getPlayer().getUniqueId() + ", " + team.getID() + ", " + player.getRank());
	}

	@Override
	public void playerLeaveTeam(Team team, TeamPlayer player) {
		database.deleteRecord(TableName.PLAYERS, "playerUUID == " + player.getPlayer().getUniqueId());
	}

	@Override
	public TeamStorage createTeamStorage(Team team) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TeamStorage createNewTeamStorage(Team team) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] sortTeamsByScore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] sortTeamsByBalance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] sortTeamsByMembers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void purgeTeamScore() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getHoloDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHoloDetails(List<String> details) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addChestClaim(Team team, Location loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeChestclaim(Location loc) {
		// TODO Auto-generated method stub

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

}
