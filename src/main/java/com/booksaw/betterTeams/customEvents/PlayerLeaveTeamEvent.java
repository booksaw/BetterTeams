package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveTeamEvent extends TeamPlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public PlayerLeaveTeamEvent(Team team, TeamPlayer teamPlayer) {
		super(team, teamPlayer, true);
	}

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

}
