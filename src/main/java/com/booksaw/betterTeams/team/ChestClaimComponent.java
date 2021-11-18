package com.booksaw.betterTeams.team;

import org.bukkit.Location;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class ChestClaimComponent extends LocationListComponent {

	@Override
	public String getSectionHeading() {
		return "chests";
	}

	@Override
	public void load(TeamStorage section) {
		load(section.getClaimedChests());
	}

	@Override
	public void save(TeamStorage storage) {
		storage.setClaimedChests(getConvertedList());
	}

	@Override
	public void add(Team team, Location component) {
		super.add(team, component);
		Team.getTeamManager().addChestClaim(team, component);
	}

	@Override
	public void remove(Team team, Location component) {
		super.remove(team, component);
		Team.getTeamManager().removeChestclaim(component);
	}

	@Override
	public void clear() {
		for (Location location : list) {
			Team.getTeamManager().removeChestclaim(location);
		}

		super.clear();

	}

}
