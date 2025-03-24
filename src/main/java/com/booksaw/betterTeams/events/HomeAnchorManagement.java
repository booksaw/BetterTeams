package com.booksaw.betterTeams.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.PlayerHomeAnchorEvent;

public class HomeAnchorManagement implements Listener {

    private final boolean checkUsePermission;

    public HomeAnchorManagement() {
        checkUsePermission = Main.plugin.getConfig().getBoolean("anchor.checkUsePermission");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRespawn(@NotNull PlayerRespawnEvent e) {
        Team temp = Team.getTeam(e.getPlayer());
        if (temp == null)
            return;
        if (!temp.isAnchored())
            return;
        TeamPlayer teamPlayer = temp.getTeamPlayer(e.getPlayer());
        if (!teamPlayer.isAnchored()) {
            return;
        }
        Location teamHome = temp.getTeamHome();
        if (teamHome == null) {
            temp.setAnchored(false);
            return;
        }
        if (checkUsePermission && !e.getPlayer().hasPermission("betterteams.anchor.use"))
            return;
        PlayerHomeAnchorEvent anchorEvent = new PlayerHomeAnchorEvent(temp, teamPlayer, teamHome);
        Bukkit.getPluginManager().callEvent(anchorEvent);;
        if(anchorEvent.isCancelled()) {
            return;
        }
        e.setRespawnLocation(teamHome);
    }
}
