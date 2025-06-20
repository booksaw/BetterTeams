package com.booksaw.betterTeams.integrations.placeholder;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.TeamManager;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author booksaw
 * Class is defined to provide an individual team placeholder
 */
public interface IndividualTeamPlaceholderProvider {

	String getPlaceholderForTeam(Team team);

	default String getPlaceholderForTeam(@NotNull Team team, @NotNull Function<TeamManager, String[]> sortingFunction) {
		// Get the sorted teams using the provided sorting function
		String[] sortedTeams = sortingFunction.apply(Team.getTeamManager());

		// Find the position of the team in the sorted array
		int position = ArrayUtils.indexOf(sortedTeams, team.getName()) + 1;

		// Return the position as a string
		return position + "";
	}
}
