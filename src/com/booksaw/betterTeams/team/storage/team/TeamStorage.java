package com.booksaw.betterTeams.team.storage.team;

import com.booksaw.betterTeams.Team;

/**
 * Used to manage the storage for a single team
 * @author booksaw
 *
 */
public abstract class TeamStorage {

	protected final Team team;

	protected TeamStorage(Team team) {
		this.team = team;
	}

	
	
}
