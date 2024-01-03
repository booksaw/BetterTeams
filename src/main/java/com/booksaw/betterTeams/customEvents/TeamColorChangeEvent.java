package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called right before the recoloring of a {@link Team}
 */
public class TeamColorChangeEvent extends TeamEvent {
    private ChatColor newColor;
    public TeamColorChangeEvent(@NotNull Team team,
                               @NotNull ChatColor newColor) {
        super(team);
        this.newColor = newColor;
    }

    public ChatColor getNewTeamColor() {
        return newColor;
    }
    public void setNewTeamColor(ChatColor newColorToSet) {
        this.newColor = newColorToSet;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
