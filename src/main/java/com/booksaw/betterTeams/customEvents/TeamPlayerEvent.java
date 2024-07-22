package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import org.bukkit.OfflinePlayer;

public abstract class TeamPlayerEvent extends TeamEvent {

	protected final TeamPlayer teamPlayer;

	public TeamPlayerEvent(Team team, TeamPlayer teamPlayer, final boolean isAsync) {
		super(team, isAsync);

		this.teamPlayer = teamPlayer;

	}

	public TeamPlayerEvent(final Team team, final TeamPlayer teamPlayer) {
		this(team, teamPlayer, true);
	}

	public TeamPlayer getTeamPlayer() {
		return teamPlayer;
	}

	public OfflinePlayer getPlayer() {
		return teamPlayer.getPlayer();
	}

}
