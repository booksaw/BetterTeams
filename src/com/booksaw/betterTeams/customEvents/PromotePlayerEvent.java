package com.booksaw.betterTeams.customEvents;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

public class PromotePlayerEvent extends RankChangePlayerEvent {

	public PromotePlayerEvent(Team team, TeamPlayer teamPlayer, PlayerRank currentRank, PlayerRank newRank) {
		super(team, teamPlayer, currentRank, newRank);
	}

	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

}
