package com.booksaw.betterTeams.team;

import org.bukkit.configuration.ConfigurationSection;

public abstract class IntTeamComponent implements TeamComponent<Integer>, VariableTeamComponent<Integer> {

	Integer value;

	@Override
	public Integer get() {
		return value;
	}

	@Override
	public void set(Integer value) {
		this.value = value;
	}

	@Override
	public void save(ConfigurationSection section) {
		section.set(getSectionHeading(), value);
	}

	@Override
	public void load(ConfigurationSection section) {
		value = section.getInt(getSectionHeading(), 0);
	}

	public abstract String getSectionHeading();

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
