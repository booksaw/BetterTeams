package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;
import org.apache.commons.lang.ArrayUtils;

public class PositionBalPlaceholderProvider implements IndividualTeamPlaceholderProvider {

    @Override
    public String getPlaceholderForTeam(Team team) {

        String[] teamsByBalance = Team.getTeamManager().sortTeamsByBalance();

        int balPosition = ArrayUtils.indexOf(teamsByBalance, team.getName()) + 1;

        return balPosition + "";

    }

}
