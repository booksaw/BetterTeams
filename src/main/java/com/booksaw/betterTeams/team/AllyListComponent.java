package com.booksaw.betterTeams.team;

import java.util.UUID;

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

		Team allyTeam = Team.getTeam(ally);
		// notifying all online members of the team
		Message message = new ReferencedFormatMessage("ally.ally", allyTeam.getDisplayName());
		team.getMembers().broadcastMessage(message);
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
