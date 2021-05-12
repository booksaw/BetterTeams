package com.booksaw.betterTeams.customEvents;

import org.bukkit.event.HandlerList;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

/**
 * Called when a player joins a team
 * 
 * @author booksaw
 *
 */
public class PlayerJoinTeamEvent extends TeamPlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public PlayerJoinTeamEvent(Team team, TeamPlayer teamPlayer) {
		super(team, teamPlayer);
	}

}
