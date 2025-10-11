package com.booksaw.betterTeams.integrations.placeholder;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.TeamManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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
		int position = 0;

		if (sortedTeams != null) {
			for (int i = 0; i < sortedTeams.length; i++) {
				if (Objects.equals(sortedTeams[i], team.getName())) {
					position = i + 1;
					break;
				}
			}
		}

		return String.valueOf(position);
	}
}
