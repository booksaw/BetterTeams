package com.booksaw.betterTeams.integrations.placeholder;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

/**
 * Class to provide all placeholders in relation to a team
 *
 * @author booksaw
 */
public final class TeamPlaceholderService {

	private TeamPlaceholderService() {
	}

	public static String getPlaceholder(String placeholder, Team team, TeamPlayer player) {
		TeamPlaceholderOptionsEnum enumValue = TeamPlaceholderOptionsEnum.getEnumValue(placeholder);

		if (enumValue == null) {
			return null;
		}

		return enumValue.applyPlaceholderProvider(team, player);

	}

}
