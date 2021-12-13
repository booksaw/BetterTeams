package com.booksaw.betterTeams.team.storage.team;

import java.util.List;

import org.bukkit.inventory.Inventory;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.database.TableName;
import com.booksaw.betterTeams.team.storage.storageManager.SQLStorageManager;

public class SQLTeamStorage extends TeamStorage {

	private final SQLStorageManager storageManager;

	public SQLTeamStorage(SQLStorageManager storageManager, Team team) {
		super(team);
		this.storageManager = storageManager;
	}

	private String getCondition() {
		return "teamID == " + team.getID();
	}

	@Override
	protected void setValue(String location, TeamStorageType storageType, Object value) {
		storageManager.getDatabase().updateRecordWhere(TableName.PLAYERS, location + " = " + value, getCondition());
	}

	@Override
	public String getString(String reference) {
		return storageManager.getDatabase().getResult(reference, TableName.PLAYERS, getCondition());
	}

	@Override
	public boolean getBoolean(String reference) {
		return Boolean
				.parseBoolean(storageManager.getDatabase().getResult(reference, TableName.PLAYERS, getCondition()));
	}

	@Override
	public double getDouble(String reference) {
		return Double.parseDouble(storageManager.getDatabase().getResult(reference, TableName.PLAYERS, getCondition()));
	}

	@Override
	public int getInt(String reference) {
		return Integer.parseInt(storageManager.getDatabase().getResult(reference, TableName.PLAYERS, getCondition()));
	}

	@Override
	public List<String> getPlayerList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPlayerList(List<String> players) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getBanList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBanList(List<String> players) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getAllyList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAllyList(List<String> players) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getAllyRequestList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAllyRequestList(List<String> players) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getEchestContents(Inventory inventory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEchestContents(Inventory inventory) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getWarps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWarps(List<String> warps) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getClaimedChests() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClaimedChests(List<String> chests) {
		// TODO Auto-generated method stub

	}

}
