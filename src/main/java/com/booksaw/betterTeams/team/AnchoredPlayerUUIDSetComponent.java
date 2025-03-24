package com.booksaw.betterTeams.team;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class AnchoredPlayerUuidSetComponent extends UuidSetComponent {

    @Override
    public void add(Team team, UUID playerUUID) {
        if (team.getTeamPlayer(Bukkit.getOfflinePlayer(playerUUID)) != null)
            set.add(playerUUID);
    }

    @Override
    public void remove(Team team, UUID playerUUID) {
        set.remove(playerUUID);
    }

    @Override
    public String getSectionHeading() {
        return "anchoredPlayers";
    }

    @Override
    public void load(TeamStorage section) {
        set.clear();
        set.addAll(section.getAnchoredPlayerList());
    }

    @Override
    public void save(TeamStorage storage) {
        storage.setAnchoredPlayerList(getConvertedList());
    }
}
