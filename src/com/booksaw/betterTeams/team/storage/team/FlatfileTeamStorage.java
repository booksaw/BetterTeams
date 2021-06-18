package com.booksaw.betterTeams.team.storage.team;

import com.booksaw.betterTeams.Team;

public class FlatfileTeamStorage extends TeamStorage {

	public FlatfileTeamStorage(Team team, boolean newTeam) {
		super(team);
	}

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

}
