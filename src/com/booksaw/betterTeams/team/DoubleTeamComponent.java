package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public abstract class DoubleTeamComponent implements TeamComponent<Double>, VariableTeamComponent<Double> {

	protected double value = 0;

	@Override
	public void add(Double amount) {
		value += amount;
	}

	@Override
	public void sub(Double amount) {
		value -= amount;
	}

	@Override
	public void mult(Double amount) {
		value *= amount;
	}

	@Override
	public void div(Double amount) {
		value = value / amount;
	}

	@Override
	public Double get() {
		return value;
	}

	@Override
	public void set(Double value) {
		this.value = value;
	}

	@Override
	public void load(TeamStorage section) {
		value = section.getDouble(getSectionHeading());
	}

	@Override
	public void save(TeamStorage storage) {
		storage.set(getSectionHeading(), value);
	}

	public abstract StoredTeamValue getSectionHeading();

}
