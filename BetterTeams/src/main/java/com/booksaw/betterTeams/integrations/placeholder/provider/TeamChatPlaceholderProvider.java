package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlayerPlaceholderProvider;
import com.booksaw.betterTeams.message.MessageManager;

public class TeamChatPlaceholderProvider implements IndividualTeamPlayerPlaceholderProvider {
	@Override
	public String getPlaceholderForTeamPlayer(Team team, TeamPlayer player) {
		if (player.isInTeamChat()) {
			return MessageManager.getMessage("placeholder.teamChat");
		} else if (player.isInAllyChat()) {
			return MessageManager.getMessage("placeholder.allyChat");
		}
		return MessageManager.getMessage("placeholder.globalChat");
	}
}
