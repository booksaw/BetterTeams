package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public interface TeamComponent<T> {

	/**
	 * @return The stored value
	 */
	public T get();

	/**
	 * @param value Set what the currently stored value is
	 */
	public void set(T value);

	/**
	 * Load the value stored in the config into this component
	 * 
	 * @param section The configuration section where all the team data is stored
	 */
	public void load(TeamStorage section);

	/**
	 * Store the loaded value to the configuration section
	 * 
	 * @param storage The storage class where all team storage takes place
	 */
	public void save(TeamStorage storage);

}
