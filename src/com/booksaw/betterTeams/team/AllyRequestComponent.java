package com.booksaw.betterTeams.team;

import java.util.UUID;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.Message;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class AllyRequestComponent extends UuidListComponent {

	@Override
	public void load(TeamStorage section) {
		load(section.getAllyRequestList());
	}

	@Override
	public void save(TeamStorage storage) {
		storage.setAllyRequestList(getConvertedList());
	}

	@Override
	public String getSectionHeading() {
		return null;
	}

	@Override
	public void add(Team team, UUID component) {
		Team t = Team.getTeam(component);
		// notifying all online owners of the team
		Message message = new ReferencedFormatMessage("ally.request", t.getDisplayName());
		t.getMembers().broadcastMessage(message);

	}

}
