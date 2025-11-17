/**
 *
 */
package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;
import com.booksaw.betterTeams.util.MoneyUtils;

/**
 * @author booksaw
 */
public class MaxMoneyPlaceholderProvider implements IndividualTeamPlaceholderProvider {

	@Override
	public String getPlaceholderForTeam(Team team) {
		return MoneyUtils.getFormattedDouble(team.getMaxMoney());
	}


}
