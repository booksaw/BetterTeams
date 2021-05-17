package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import org.bukkit.OfflinePlayer;

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
