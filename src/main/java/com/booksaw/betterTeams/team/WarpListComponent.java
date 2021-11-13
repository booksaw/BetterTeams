package com.booksaw.betterTeams.team;

import java.util.ArrayList;

import com.booksaw.betterTeams.Warp;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class WarpListComponent extends ListTeamComponent<Warp> {

	@Override
	public void load(TeamStorage section) {
		load(section.getWarps());
	}

	@Override
	public void save(TeamStorage storage) {
		storage.setWarps(getConvertedList());
	}

	@Override
	public String getSectionHeading() {
		return "warps";
	}

	@Override
	public Warp fromString(String str) {
		String[] split = str.split(";");
		return new Warp(split);
	}

	@Override
	public String toString(Warp component) {
		return component.toString();
	}

	public Warp get(String name) {
		for(Warp warp : new ArrayList<>(list)) {
			if(warp.getName().equalsIgnoreCase(name)) {
				return warp;
			}
		}
		return null;
	}

}
