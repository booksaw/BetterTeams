package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;
import com.booksaw.betterTeams.message.MessageManager;

public class HasHomePlaceholderProvider implements IndividualTeamPlaceholderProvider {
	@Override
	public String getPlaceholderForTeam(Team team) {
		return team.getTeamHome() != null ? MessageManager.getMessage("placeholder.hasHome") : MessageManager.getMessage("placeholder.noHome");
	}
}
