package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DisbandTeamEvent extends TeamEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public DisbandTeamEvent(Team team) {
        super(team);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
