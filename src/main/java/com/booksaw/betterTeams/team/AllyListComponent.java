package com.booksaw.betterTeams.team;

import java.util.List;
import java.util.UUID;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.Message;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class AllyListComponent extends UuidListComponent {

	@Override
	public String getSectionHeading() {
		return "allies";
	}

	@Override
	public void add(Team team, UUID ally) {
		super.add(team, ally);

		team.getStorage().addAlly(ally);
	}

	@Override
	public void remove(Team team, UUID component) {
		super.remove(team, component);

		team.getStorage().removeAlly(component);
	}

	@Override
	public void load(TeamStorage section) {
		load(section.getAllyList());
	}

	@Override
	public void save(TeamStorage storage) {
		storage.setAllyList(getConvertedList());
	}

}
