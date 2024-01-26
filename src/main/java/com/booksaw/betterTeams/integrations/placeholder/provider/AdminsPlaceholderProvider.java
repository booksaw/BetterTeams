package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;

import java.util.List;
import java.util.stream.Collectors;

public class AdminsPlaceholderProvider implements IndividualTeamPlaceholderProvider {

    @Override
    public String getPlaceholderForTeam(Team team) {
        // Convert the List<TeamPlayer> to a List<String> of admin names
        List<String> adminNamesList = team.getRank(PlayerRank.ADMIN).stream().map(teamPlayer -> teamPlayer.getPlayer().getName()).collect(Collectors.toList());

        return String.join(", ", adminNamesList);

    }

}
