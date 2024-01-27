package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;

import java.util.List;
import java.util.stream.Collectors;

public class OwnersPlaceholderProvider implements IndividualTeamPlaceholderProvider {

    @Override
    public String getPlaceholderForTeam(Team team) {
        // Convert the List<TeamPlayer> to a List<String> of owner names
        List<String> ownerNamesList = team.getRank(PlayerRank.OWNER).stream().map(teamPlayer -> teamPlayer.getPlayer().getName()).collect(Collectors.toList());

        return String.join(", ", ownerNamesList);

    }

}
