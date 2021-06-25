package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public abstract class IntTeamComponent implements TeamComponent<Integer>, VariableTeamComponent<Integer> {

	protected Integer value = 0;

	@Override
	public Integer get() {
		return value;
	}

	@Override
	public void set(Integer value) {
		this.value = value;
	}

	@Override
	public void save(TeamStorage section) {
		section.set(getSectionHeading(), value);
	}

	@Override
	public void load(TeamStorage section) {
		value = section.getInt(getSectionHeading());
	}

	public abstract StoredTeamValue getSectionHeading();

	@Override
	public void add(Integer amount) {
		set(get() + amount);
	}

	@Override
	public void sub(Integer amount) {
		set(get() - amount);
	}

	@Override
	public void mult(Integer amount) {
		set(get() * amount);
	}

	@Override
	public void div(Integer amount) {
		set(get() / amount);
	}

}
