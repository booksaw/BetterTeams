package com.booksaw.betterTeams.team;

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

}
