package com.booksaw.betterTeams.team.storage.storageManager;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.database.BetterTeamsDatabase;
import com.booksaw.betterTeams.database.TableName;
import com.booksaw.betterTeams.team.TeamManager;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class SQLStorageManager extends TeamManager {

	BetterTeamsDatabase database;

	public SQLStorageManager() {
		setupDatabaseConnection();
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerNewTeam(Team team, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void deleteTeamStorage(Team team) {
		// TODO Auto-generated method stub

	}

	@Override
	public void teamNameChange(Team team, String newName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerJoinTeam(Team team, TeamPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerLeaveTeam(Team team, TeamPlayer player) {
		// TODO Auto-generated method stub

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

}
