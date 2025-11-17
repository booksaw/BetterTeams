package com.booksaw.betterTeams.integrations.placeholder;

import com.booksaw.betterTeams.Team;

/**
 * Interface for placeholders that require additional data
 */
public interface IndividualTeamWithDataPlaceholderProvider {

	String getPlaceholderForTeam(Team team, String data);
}
