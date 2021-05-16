package com.booksaw.betterTeams.team;

import org.bukkit.configuration.ConfigurationSection;

public abstract class DoubleTeamComponent implements TeamComponent<Double>, VariableTeamComponent<Double> {

	protected double value;

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
	public void load(ConfigurationSection section) {
		value = section.getDouble(getSectionHeading(), 0);
	}

	@Override
	public void save(ConfigurationSection section) {
		section.set(getSectionHeading(), value);
	}

	public abstract String getSectionHeading();

}
