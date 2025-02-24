package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called right before the renaming of a {@link Team}
 */
public class TeamNameChangeEvent extends TeamEvent {
    private String newName;

    public TeamNameChangeEvent(@NotNull Team team,
                               @NotNull String newName) {
        super(team);
        this.newName = newName;
    }

    public String getNewTeamName() {
        return newName;
    }
    public void setNewTeamName(String newNameToSet) {
        this.newName = newNameToSet;
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
