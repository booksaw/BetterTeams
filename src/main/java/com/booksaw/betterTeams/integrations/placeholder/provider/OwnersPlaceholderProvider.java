package com.booksaw.betterTeams.integrations.placeholder.provider;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.integrations.placeholder.IndividualTeamPlaceholderProvider;

import java.util.ArrayList;
import java.util.List;

public class OwnersPlaceholderProvider implements IndividualTeamPlaceholderProvider {

    @Override
    public String getPlaceholderForTeam(Team team) {
        List<String> ownersList = new ArrayList<>();
        for (TeamPlayer teamplayer : team.getMembers().get()) {
            if (teamplayer.getRank().equals(PlayerRank.OWNER)) {
                ownersList.add(teamplayer.getPlayer().getName());
            }
        }

        return String.join(", ", ownersList);

    }

}
