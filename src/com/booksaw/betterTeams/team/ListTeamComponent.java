package com.booksaw.betterTeams.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public abstract class ListTeamComponent<T> implements TeamComponent<List<T>> {

	protected List<T> list;

	protected ListTeamComponent() {
		list = new ArrayList<>();
	}

	@Override
	public List<T> get() {
		return list;
	}

	@Override
	public void set(List<T> list) {
		this.list = list;
	}

	@Override
	public void load(ConfigurationSection section) {
		List<String> componentStrings = section.getStringList(getSectionHeading());
		list = new ArrayList<>();

		for (String str : componentStrings) {
			list.add(fromString(str));
		}
	}

	@Override
	public void save(ConfigurationSection section) {
		List<String> componentStrings = new ArrayList<>();

		for (T component : list) {
			componentStrings.add(toString(component));
		}

		section.set(getSectionHeading(), componentStrings);
	}

	/**
	 * Used to add a component to this component list
	 * 
	 * @param component The component to add
	 */
	public void add(T component) {
		list.add(component);
	}

	/**
	 * Used to remove a component from this component list
	 * 
	 * @param component The component to remove
	 */
	public void remove(T component) {
		list.remove(component);
	}

	/**
	 * Used to check if the component provided is stored within this component
	 * 
	 * @param component The component to check
	 * @return If the component is within this component list
	 */
	public boolean contains(T component) {
		return list.contains(component);
	}

	/**
	 * @return The reference within the team storage where the data is stored
	 */
	public abstract String getSectionHeading();

	/**
	 * Used to convert a string into the correct component for this type
	 * 
	 * @param str The string to convert
	 * @return The value to be added to the list
	 */
	public abstract T fromString(String str);

	/**
	 * Used to convert a component of the list to a string for saving
	 * 
	 * @param component The component to convert
	 * @return The converted component
	 */
	public abstract String toString(T component);

}
