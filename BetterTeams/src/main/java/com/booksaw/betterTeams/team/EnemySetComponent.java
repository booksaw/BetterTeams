package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

import java.util.UUID;

public class EnemySetComponent extends UuidSetComponent {

	@Override
	public String getSectionHeading() {
		return "enemies";
	}

	@Override
	public void add(Team team, UUID enemy) {
		super.add(team, enemy);

		team.getStorage().addEnemy(enemy);
	}

	@Override
	public void remove(Team team, UUID enemy) {
		super.remove(team, enemy);

		team.getStorage().removeEnemy(enemy);
	}

	@Override
	public void load(TeamStorage section) {
		load(section.getEnemyList());
	}

	@Override
	public void save(TeamStorage storage) {
		storage.setEnemyList(getConvertedList());
	}

}
