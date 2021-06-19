package com.booksaw.betterTeams.team.storage.team;

import java.util.List;

import org.bukkit.inventory.Inventory;

import com.booksaw.betterTeams.Team;

public class FlatfileTeamStorage extends TeamStorage {

	public FlatfileTeamStorage(Team team, boolean newTeam) {
		super(team);
	}

//	public ConfigurationSection getConfig() {
//	ConfigurationSection section = getTeamManager().getTeamStorage().getConfigurationSection(getConfigPath());
//	if (section == null) {
//		section = getTeamManager().getTeamStorage().createSection(getConfigPath());
//	}
//	return section;
//}

//private String getConfigPath() {
//	return "team." + id;
//}
	
	@Override
	protected void setValue(String location, TeamStorageType storageType, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getString(String reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getBoolean(String reference) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getDouble(String reference) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInt(String reference) {
		// TODO Auto-generated method stub
		return 0;
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
	public void getEchestContents(Inventory inventory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEchestContents(Inventory inventory) {
		// TODO Auto-generated method stub
		
	}

}
