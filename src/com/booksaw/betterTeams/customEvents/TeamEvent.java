package com.booksaw.betterTeams.customEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.booksaw.betterTeams.Team;

/**
 * Wrapper for all events which contain a team
 * 
 * @author booksaw
 *
 */
public abstract class TeamEvent extends Event implements Cancellable {

	protected final Team team;

	public TeamEvent(Team team) {
		this.team = team;
	}

	public Team getTeam() {
		return team;
	}

	protected boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
