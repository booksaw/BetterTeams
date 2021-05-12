package com.booksaw.betterTeams.customEvents;

import org.bukkit.event.HandlerList;

import com.booksaw.betterTeams.Team;

public class DisbandTeamEvent extends TeamEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public DisbandTeamEvent(Team team) {
		super(team);
	}

}
