/**
 *
 */
package com.booksaw.betterTeams.integrations.placeholder;

import com.booksaw.betterTeams.Team;

/**
 * @author booksaw
 * Class is defined to provide an individual team placeholder
 */
public interface IndividualTeamPlaceholderProvider {

	String getPlaceholderForTeam(Team team);

}
