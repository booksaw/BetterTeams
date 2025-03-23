package com.booksaw.betterTeams.team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.team.storage.team.SQLTeamStorage;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class AnchoredPlayerUUIDSetComponent extends UuidSetComponent {

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

    public void load(TeamStorage section, Team team) {
        if(section instanceof SQLTeamStorage) {
            set.clear();
            List<UUID> lst = new ArrayList<UUID>();
            for(TeamPlayer player : team.getMembers().getClone()) {
                if(player.isAnchored())
                    lst.add(player.getPlayerUUID());
            }
            set.addAll(lst);
        }
        load(section);
        return;
    }

    @Override
    public void save(TeamStorage storage) {
        storage.setAnchoredPlayerList(getConvertedList());
    }
}
