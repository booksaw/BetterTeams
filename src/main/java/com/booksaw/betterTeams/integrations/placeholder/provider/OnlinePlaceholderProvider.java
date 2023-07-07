/**
 * 
 */
package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;

/**
 * @author booksaw
 *
 */
public class OnlinePlaceholderProvider implements IndividualTeamPlaceholderProvider {

	@Override
	public String getPlaceholderForTeam(Team team) {
		return Integer.toString(team.getOnlineMembers().size());
	}
	
}
