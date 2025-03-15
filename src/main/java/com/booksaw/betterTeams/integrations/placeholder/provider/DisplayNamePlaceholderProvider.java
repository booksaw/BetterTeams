/**
 *
 */
package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;
import com.booksaw.betterTeams.message.MessageManager;

/**
 * @author booksaw
 */
public class DisplayNamePlaceholderProvider implements IndividualTeamPlaceholderProvider {

	@Override
	public String getPlaceholderForTeam(Team team) {
		return MessageManager.getMessage("placeholder.displayname", team.getDisplayName());
	}

}
