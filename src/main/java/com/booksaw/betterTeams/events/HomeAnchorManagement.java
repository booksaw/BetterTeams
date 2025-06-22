package com.booksaw.betterTeams.events;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.PlayerHomeAnchorEvent;

public class HomeAnchorManagement implements Listener {

    private final JavaPlugin plugin;
    private final EventPriority defaultPriority = EventPriority.NORMAL;
    private final EventPriority priority;
    private final EnumSet<EventPriority> allowedPriorities = EnumSet.of(
            EventPriority.HIGH,
            EventPriority.HIGHEST,
            EventPriority.NORMAL,
            EventPriority.LOW,
            EventPriority.LOWEST);

    private final boolean checkUsePermission;
    private final boolean checkAnchoredPlayer;

    public HomeAnchorManagement(Main plugin) {
        this.plugin = plugin;
        this.checkUsePermission = plugin.getConfig().getBoolean("anchor.checkUsePermission");
        this.checkAnchoredPlayer = plugin.getConfig().getBoolean("anchor.checkAnchoredPlayer");
        this.priority = getConfiguredPriority();
    }

    private EventPriority getConfiguredPriority() {
        String priorityStr = plugin.getConfig().getString("anchor.priority", "NORMAL").toUpperCase();
        try {
            return allowedPriorities.contains(EventPriority.valueOf(priorityStr))
                    ? EventPriority.valueOf(priorityStr)
                    : defaultPriority;
        } catch (IllegalArgumentException | NullPointerException e) {
            return defaultPriority;
        }
    }

    public void registerEvent() {
		Bukkit.getPluginManager().registerEvent(PlayerRespawnEvent.class, this, priority,
                (listener, event) -> {
                    if (event instanceof PlayerRespawnEvent) {
                        onRespawn((PlayerRespawnEvent) event);
                    }
                },
                plugin);
    }

    public void onRespawn(@NotNull PlayerRespawnEvent e) {
		if (e.isAnchorSpawn() && plugin.getConfig().getBoolean("ignoreObsidianAnchorRespawn", false))
			return;
		if (e.isBedSpawn() && plugin.getConfig().getBoolean("ignoreBedRespawn", false))
			return;

        Team team = Team.getTeam(e.getPlayer());
        if (team == null || !team.isAnchored()) return;

        TeamPlayer teamPlayer = team.getTeamPlayer(e.getPlayer());
		if (teamPlayer == null) return;
        if (checkAnchoredPlayer && !teamPlayer.isAnchored()) return;

        // This goes before the team home for ensuring it exists even after this
        if (checkUsePermission && !e.getPlayer().hasPermission("betterteams.anchor.use")) return;

        Location teamHome = team.getTeamHome();
        if (teamHome == null) return;

        PlayerHomeAnchorEvent anchorEvent = new PlayerHomeAnchorEvent(team, teamPlayer, teamHome, e);
        Bukkit.getPluginManager().callEvent(anchorEvent);

        if (!anchorEvent.isCancelled()) e.setRespawnLocation(anchorEvent.getLocation());
    }
}
