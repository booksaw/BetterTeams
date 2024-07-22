package com.booksaw.betterTeams.team;

import java.util.UUID;

public abstract class UuidSetComponent extends SetTeamComponent<UUID> {

	@Override
	public UUID fromString(String str) {
		return UUID.fromString(str);
	}

	@Override
	public String toString(UUID component) {
		return component.toString();
	}

}
