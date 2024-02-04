package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;
import org.apache.commons.lang.ArrayUtils;

public class PositionMembersPlaceholderProvider implements IndividualTeamPlaceholderProvider {

    @Override
    public String getPlaceholderForTeam(Team team) {

        String[] teamsByMembers = Team.getTeamManager().sortTeamsByMembers();

        int membersPosition = ArrayUtils.indexOf(teamsByMembers, team.getName()) + 1;

        return membersPosition + "";

    }

}
