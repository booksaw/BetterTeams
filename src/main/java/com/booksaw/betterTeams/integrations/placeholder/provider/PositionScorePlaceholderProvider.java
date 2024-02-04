package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;
import org.apache.commons.lang.ArrayUtils;

public class PositionScorePlaceholderProvider implements IndividualTeamPlaceholderProvider {

    @Override
    public String getPlaceholderForTeam(Team team) {

        String[] teamsByScore = Team.getTeamManager().sortTeamsByScore();

        int scorePosition = ArrayUtils.indexOf(teamsByScore, team.getName()) + 1;

        return scorePosition + "";

    }

}
