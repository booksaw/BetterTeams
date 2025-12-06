package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;

public class ColorNamePlaceholderProvider implements IndividualTeamPlaceholderProvider {
	@Override
	public String getPlaceholderForTeam(com.booksaw.betterTeams.Team team) {
		return team.getColor().name();
	}
}
