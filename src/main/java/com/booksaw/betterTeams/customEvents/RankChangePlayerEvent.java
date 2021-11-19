package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

public abstract class RankChangePlayerEvent extends TeamPlayerEvent {

	private final PlayerRank currentRank, newRank;

	public RankChangePlayerEvent(Team team, TeamPlayer teamPlayer, PlayerRank currentRank, PlayerRank newRank) {
		super(team, teamPlayer);
		this.currentRank = currentRank;
		this.newRank = newRank;
	}

	public PlayerRank getCurrentRank() {
		return currentRank;
	}

	public PlayerRank getNewRank() {
		return newRank;
	}

}
