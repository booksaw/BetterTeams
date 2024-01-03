package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called right before the creation of a {@link Team}
 */
public class CreateTeamEvent extends TeamEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public CreateTeamEvent(Team team) {
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
