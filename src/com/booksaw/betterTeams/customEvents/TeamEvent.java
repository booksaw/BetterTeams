package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Wrapper for all events which contain a team
 *
 * @author booksaw
 */
public abstract class TeamEvent extends Event implements Cancellable {

    protected final Team team;
    protected boolean cancelled = false;

    public TeamEvent(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
