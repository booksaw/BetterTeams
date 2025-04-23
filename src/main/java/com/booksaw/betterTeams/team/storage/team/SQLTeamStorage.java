package com.booksaw.betterTeams.team.storage.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.database.TableName;
import com.booksaw.betterTeams.team.storage.storageManager.SQLStorageManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
		List<TeamPlayer> toReturn = new ArrayList<>();

		try (PreparedStatement ps = storageManager.getDatabase().selectWhere("*", TableName.PLAYERS, getCondition())) {
			ResultSet result = ps.executeQuery();
			if (!result.first()) {
				return toReturn;
			}
			do {

				toReturn.add(new TeamPlayer(Bukkit.getOfflinePlayer(UUID.fromString(result.getString("playerUUID"))),
						PlayerRank.getRank((result.getInt("playerRank"))), result.getString("title"),
						result.getBoolean("anchor")));

			} while (result.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public List<UUID> getAnchoredPlayerList() {
		List<UUID> toReturn = new ArrayList<>();

		try (PreparedStatement ps = storageManager.getDatabase().selectWhere("*", TableName.PLAYERS, getCondition())) {
			ResultSet result = ps.executeQuery();
			if (!result.first()) {
				return toReturn;
			}
			do {
				if(result.getBoolean("anchor"))
					toReturn.add(UUID.fromString(result.getString("playerUUID")));
			} while (result.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public List<String> getBanList() {

		List<String> toReturn = new ArrayList<>();

		try (PreparedStatement ps = storageManager.getDatabase().selectWhere("*", TableName.BANS, getCondition())) {
			ResultSet result = ps.executeQuery();
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

	private List<String> getTeamList(final TableName table) {

		List<String> toReturn = new ArrayList<>();

		try (PreparedStatement ps = storageManager.getDatabase().selectWhere("*", table,
				"team1ID LIKE '" + team.getID() + "' OR team2ID LIKE '" + team.getID() + "'")) {
			ResultSet result = ps.executeQuery();
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
	public List<String> getAllyList() {
		return getTeamList(TableName.ALLIES);
	}

	@Override
	public List<String> getAllyRequestList() {

		List<String> toReturn = new ArrayList<>();

		try (PreparedStatement ps = storageManager.getDatabase().selectWhere("*", TableName.ALLYREQUESTS,
				"receivingTeamID LIKE '" + team.getID() + "'")) {

			ResultSet result = ps.executeQuery();
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
		if (result == null || result.isEmpty()) {
			return;
		}

		Utils.deserializeIntoInventory(inventory, result);
	}

	@Override
	public void setEchestContents(Inventory inventory) {
		String serial = Utils.serializeInventory(inventory);
		serial = serial.replace("\\", "\\\\");
		serial = serial.replace("\"", "\\\"");
		serial = "\"" + serial + "\"";
		storageManager.getDatabase().executeStatement("UPDATE ? SET echest = ? WHERE ?",
				TableName.TEAM.toString(), serial, getCondition());

	}

	@Override
	public List<String> getWarps() {

		List<String> toReturn = new ArrayList<>();

		try (PreparedStatement ps = storageManager.getDatabase().selectWhere("*", TableName.WARPS, getCondition())) {

			ResultSet result = ps.executeQuery();
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

		List<String> toReturn = new ArrayList<>();

		try (PreparedStatement ps = storageManager.getDatabase().selectWhere("*", TableName.CHESTCLAIMS, getCondition())) {

			ResultSet result = ps.executeQuery();
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
	public void setAnchor(TeamPlayer player, boolean anchor) {
		storageManager.getDatabase().updateRecordWhere(TableName.PLAYERS, "anchor = " + anchor,
				"playerUUID = '" + player.getPlayer().getUniqueId() + "'");
	}

	@Override
	public void setPlayerList(List<String> players) {
		// not needed
	}

	@Override
	public void setAnchoredPlayerList(List<String> players) {
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
