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

		Team allyTeam = Team.getTeam(ally);

		// notifying all online members of the team
		List<String> channelsToUse = Main.plugin.getConfig().getStringList("onAllyMessageChannel");
		Message message = new ReferencedFormatMessage("ally.ally", allyTeam.getDisplayName());

		if (channelsToUse.isEmpty() || channelsToUse.contains("CHAT")) {
			team.getMembers().broadcastMessage(message);
		}
		if (channelsToUse.isEmpty() || channelsToUse.contains("TITLE")) {
			team.getMembers().broadcastTitle(message);
		}

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
