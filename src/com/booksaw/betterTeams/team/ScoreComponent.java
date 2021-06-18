package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;

public class ScoreComponent extends IntTeamComponent {

	@Override
	public StoredTeamValue getSectionHeading() {
		return StoredTeamValue.SCORE;
	}

}
