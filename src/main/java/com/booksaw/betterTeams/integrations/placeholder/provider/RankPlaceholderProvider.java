/**
 *
 */
package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlayerPlaceholderProvider;
import com.booksaw.betterTeams.message.MessageManager;

/**
 * @author booksaw
 *
 */
public class RankPlaceholderProvider implements IndividualTeamPlayerPlaceholderProvider {

	@Override
	public String getPlaceholderForTeamPlayer(Team team, TeamPlayer player) {

		switch (player.getRank()) {
			case ADMIN:
				return MessageManager.getMessage("placeholder.admin");
			case OWNER:
				return MessageManager.getMessage("placeholder.owner");
			default:
				return MessageManager.getMessage("placeholder.default");
		}

	}

}