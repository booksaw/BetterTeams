package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;
import com.booksaw.betterTeams.team.TeamManager;

public class PositionMembersPlaceholderProvider implements IndividualTeamPlaceholderProvider {

	@Override
	public String getPlaceholderForTeam(Team team) {
		return getPlaceholderForTeam(team, TeamManager::sortTeamsByMembers);
	}

}
