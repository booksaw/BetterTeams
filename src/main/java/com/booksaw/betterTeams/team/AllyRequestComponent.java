package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.Message;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

import java.util.UUID;

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
		return "allyrequests";
	}

	@Override
	public void add(Team team, UUID component) {
		super.add(team, component);

		Team t = Team.getTeam(component);
		// notifying all online owners of the team
		Message message = new ReferencedFormatMessage("ally.request", t.getDisplayName());
		team.getMembers().broadcastMessage(message);
		
		team.getStorage().addAllyRequest(component);

	}
	
	@Override
	public void remove(Team team, UUID component) {
		super.remove(team, component);
		
		team.getStorage().removeAllyRequest(component);
	}

}
