package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called right after the renaming of a {@link Team}
 */
public class TeamNameChangedEvent extends TeamEvent {
    private final String previousName;
    public TeamNameChangedEvent(@NotNull Team team,
                                @NotNull String previousName) {
        super(team);
        this.previousName = previousName;
    }

    public String getPreviousTeamName() {
        return previousName;
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
