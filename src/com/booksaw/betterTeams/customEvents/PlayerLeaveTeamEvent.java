package com.booksaw.betterTeams.customEvents;

import org.bukkit.event.HandlerList;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

public class PlayerLeaveTeamEvent extends TeamPlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public PlayerLeaveTeamEvent(Team team, TeamPlayer teamPlayer) {
		super(team, teamPlayer);
	}

}
