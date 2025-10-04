package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamWithDataPlaceholderProvider;
import com.booksaw.betterTeams.message.MessageManager;

public class MetaPlaceholderProvider implements IndividualTeamWithDataPlaceholderProvider {
	@Override
	public String getPlaceholderForTeam(Team team, String data) {
		return team.getMeta().get().get(data).orElse(MessageManager.getMessage("placeholder.noMeta"));
	}
}
