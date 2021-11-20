package com.booksaw.betterTeams.team.storage.storageManager;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.database.api.Database;
import com.booksaw.betterTeams.team.TeamManager;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class SQLStorageManager extends TeamManager {

	Database database;

	public SQLStorageManager() {
		setupDatabaseConnection();
	}

	private void setupDatabaseConnection() {
		database = new Database();
		database.setupConnectionFromConfiguration(Main.plugin.getConfig().getConfigurationSection("database"));
	}

	@Override
	public void disable() {
		database.closeConnection();
	}
	
	@Override
	public UUID getClaimingTeamUUID(Location location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTeam(UUID uuid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTeam(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInTeam(OfflinePlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UUID getTeamUUID(OfflinePlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID getTeamUUID(String name) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub

	}

}
