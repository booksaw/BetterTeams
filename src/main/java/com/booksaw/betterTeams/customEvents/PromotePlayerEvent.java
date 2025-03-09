package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PromotePlayerEvent extends RankChangePlayerEvent {

	public PromotePlayerEvent(Team team, TeamPlayer teamPlayer, PlayerRank currentRank, PlayerRank newRank) {
		super(team, teamPlayer, currentRank, newRank, true);
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

}
