package com.booksaw.betterTeams.customEvents;

import org.bukkit.OfflinePlayer;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

public abstract class TeamPlayerEvent extends TeamEvent {

	protected final TeamPlayer teamPlayer;

	public TeamPlayerEvent(Team team, TeamPlayer teamPlayer) {
		super(team);

		this.teamPlayer = teamPlayer;

	}

	public TeamPlayer getTeamPlayer() {
		return teamPlayer;
	}

	public OfflinePlayer getPlayer() {
		return teamPlayer.getPlayer();
	}

}
