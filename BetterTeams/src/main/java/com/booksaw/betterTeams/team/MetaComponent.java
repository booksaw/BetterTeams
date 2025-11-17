package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.team.meta.TeamMeta;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class MetaComponent implements TeamComponent<TeamMeta>{

	private TeamMeta meta = new TeamMeta();

	@Override
	public TeamMeta get() {
		return meta;
	}

	@Override
	public void set(TeamMeta value) {
		meta = value;
	}

	@Override
	public void load(TeamStorage section) {
		meta.load(section.getRawMeta());
	}

	@Override
	public void save(TeamStorage storage) {
		storage.saveMeta(meta);
	}
}
