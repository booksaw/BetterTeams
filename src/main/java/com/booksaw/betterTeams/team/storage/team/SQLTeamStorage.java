package com.booksaw.betterTeams.team.storage.team;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.Inventory;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.Utils;
import com.booksaw.betterTeams.Warp;
import com.booksaw.betterTeams.database.TableName;
import com.booksaw.betterTeams.team.storage.storageManager.SQLStorageManager;

public class SQLTeamStorage extends TeamStorage {

	private final SQLStorageManager storageManager;

	public SQLTeamStorage(SQLStorageManager storageManager, Team team) {
		super(team);
		this.storageManager = storageManager;
	}

	private String getCondition() {
		return "teamID LIKE '" + team.getID() + "'";
	}

	@Override
	protected void setValue(String location, TeamStorageType storageType, Object value) {
		storageManager.getDatabase().updateRecordWhere(TableName.TEAM, location + " = '" + value.toString() + "'",
				getCondition());
	}

	@Override
	public String getString(String reference) {
		return storageManager.getDatabase().getResult(reference, TableName.TEAM, getCondition());
	}

	@Override
	public boolean getBoolean(String reference) {
		String result = storageManager.getDatabase().getResult(reference, TableName.TEAM, getCondition());
		if (result.equals("1")) {
			return true;
		} else if (result.equals("0")) {
			return false;
		}
		return Boolean.parseBoolean(result);
	}

	@Override
	public double getDouble(String reference) {
		return Double.parseDouble(storageManager.getDatabase().getResult(reference, TableName.TEAM, getCondition()));
	}

	@Override
	public int getInt(String reference) {
		return Integer.parseInt(storageManager.getDatabase().getResult(reference, TableName.TEAM, getCondition()));
	}

	@Override
	public List<TeamPlayer> getPlayerList() {
		ResultSet result = storageManager.getDatabase().selectWhere("*", TableName.PLAYERS, getCondition());

		List<TeamPlayer> toReturn = new ArrayList<>();

		try {
			if (!result.first()) {
				return toReturn;
			}
			do {

				toReturn.add(new TeamPlayer(Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerUUID"))),
						PlayerRank.getRank((result.getInt("playerRank"))), result.getString("title")));

			} while (result.next());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public List<String> getBanList() {
		ResultSet result = storageManager.getDatabase().selectWhere("*", TableName.BANS, getCondition());

		List<String> toReturn = new ArrayList<>();

		try {
			if (!result.first()) {
				return toReturn;
			}
			do {

				toReturn.add(result.getString("playerUUID"));

			} while (result.next());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public List<String> getAllyList() {
		ResultSet result = storageManager.getDatabase().selectWhere("*", TableName.ALLIES,
				"team1ID LIKE '" + team.getID() + "' OR team2ID LIKE '" + team.getID() + "'");

		List<String> toReturn = new ArrayList<>();

		try {
			if (!result.first()) {
				return toReturn;
			}
			do {
				String t1 = result.getString("team1ID");
				String toAdd = (t1.equals(team.getID().toString())) ? result.getString("team2ID") : t1;
				if (!toReturn.contains(toAdd)) {
					toReturn.add(toAdd);
				}

			} while (result.next());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public List<String> getAllyRequestList() {
		ResultSet result = storageManager.getDatabase().selectWhere("*", TableName.ALLYREQUESTS,
				"receivingTeamID LIKE '" + team.getID() + "'");

		List<String> toReturn = new ArrayList<>();

		try {
			if (!result.first()) {
				return toReturn;
			}
			do {
				toReturn.add(result.getString("requestingTeamID"));

			} while (result.next());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public void getEchestContents(Inventory inventory) {

		String result = storageManager.getDatabase().getResult("echest", TableName.TEAM, getCondition());
		if (result == null || result.length() == 0) {
			return;
		}
		try {
			Utils.deserializeInventory(inventory, result);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setEchestContents(Inventory inventory) {
		storageManager.getDatabase().updateRecordWhere(TableName.TEAM,
				"echest = \"" + Utils.serializeInventory(inventory) + "\"", getCondition());

	}

	@Override
	public List<String> getWarps() {
		ResultSet result = storageManager.getDatabase().selectWhere("*", TableName.WARPS, getCondition());

		List<String> toReturn = new ArrayList<>();

		try {
			if (!result.first()) {
				return toReturn;
			}
			do {

				toReturn.add(result.getString("warpInfo"));

			} while (result.next());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public List<String> getClaimedChests() {
		ResultSet result = storageManager.getDatabase().selectWhere("*", TableName.CHESTCLAIMS, getCondition());

		List<String> toReturn = new ArrayList<>();

		try {
			if (!result.first()) {
				return toReturn;
			}
			do {
				toReturn.add(result.getString("chestLoc"));

			} while (result.next());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public void addBan(UUID component) {
		storageManager.getDatabase().insertRecord(TableName.BANS, "playerUUID, teamID",
				"'" + component + "', '" + team.getID() + "'");
	}

	@Override
	public void removeBan(UUID component) {
		storageManager.getDatabase().deleteRecord(TableName.BANS, "playerUUID LIKE '" + component.toString() + "'");
	}

	@Override
	public void addAlly(UUID ally) {
		storageManager.getDatabase().insertRecord(TableName.ALLIES, "team1ID, team2ID",
				"'" + team.getID() + "', '" + ally + "'");
	}

	@Override
	public void removeAlly(UUID ally) {
		storageManager.getDatabase().deleteRecord(TableName.ALLIES,
				"(team1ID LIKE '" + team.getID() + "' AND team2ID LIKE '" + ally + "') OR (team1ID LIKE '" + ally
						+ "' AND team2ID LIKE '" + team.getID() + "')");
	}

	@Override
	public void addAllyRequest(UUID requesting) {
		storageManager.getDatabase().insertRecord(TableName.ALLYREQUESTS, "receivingTeamID, requestingTeamID",
				"'" + team.getID() + "', '" + requesting + "'");
	}

	@Override
	public void removeAllyRequest(UUID requesting) {
		storageManager.getDatabase().deleteRecord(TableName.ALLYREQUESTS,
				"receivingTeamID LIKE '" + team.getID() + "' AND requestingTeamID LIKE '" + requesting + "'");
	}

	@Override
	public void addWarp(Warp component) {
		storageManager.getDatabase().insertRecord(TableName.WARPS, "teamID, warpInfo",
				"'" + team.getID() + "', '" + component.toString() + "'");
	}

	@Override
	public void removeWarp(Warp component) {
		storageManager.getDatabase().deleteRecord(TableName.WARPS,
				getCondition() + " AND warpInfo LIKE '" + component.toString() + "'");
	}

	@Override
	public void promotePlayer(TeamPlayer promotePlayer) {
		changeRank(promotePlayer);
	}

	@Override
	public void demotePlayer(TeamPlayer demotePlayer) {
		changeRank(demotePlayer);
	}

	private void changeRank(TeamPlayer player) {
		storageManager.getDatabase().updateRecordWhere(TableName.PLAYERS, "playerRank = " + player.getRank().value,
				"playerUUID = '" + player.getPlayer().getUniqueId() + "'");

	}

	@Override
	public void setTitle(TeamPlayer player) {
		storageManager.getDatabase().updateRecordWhere(TableName.PLAYERS, "title = '" + player.getTitle() + "'",
				"playerUUID = '" + player.getPlayer().getUniqueId() + "'");
	}

	@Override
	public void setPlayerList(List<String> players) {
		// not needed
	}

	@Override
	public void setBanList(List<String> players) {
		// not needed
	}

	@Override
	public void setAllyList(List<String> players) {
		// not needed
	}

	@Override
	public void setAllyRequestList(List<String> players) {
		// not needed
	}

	@Override
	public void setWarps(List<String> warps) {
		// not needed
	}

	@Override
	public void setClaimedChests(List<String> chests) {
		// not needed
	}

}
