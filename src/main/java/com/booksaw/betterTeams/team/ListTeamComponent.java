package com.booksaw.betterTeams.team;

import java.util.ArrayList;
import java.util.List;

import com.booksaw.betterTeams.Team;

public abstract class ListTeamComponent<T> implements TeamComponent<List<T>> {

	protected List<T> list;

	protected ListTeamComponent() {
		list = new ArrayList<>();
	}

	@Override
	public List<T> get() {
		return list;
	}

	/**
	 * Used to get a clone of the stored list, this can be used to avoid concurrent
	 * modification
	 * 
	 * @return A clone of the stored list
	 */
	public List<T> getClone() {
		return new ArrayList<>(list);
	}

	@Override
	public void set(List<T> list) {
		this.list = list;
	}

	/**
	 * @return The number of members stored within the list
	 */
	public int size() {
		return list.size();
	}

	/**
	 * @return If the list is empty
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	public void load(List<String> strList) {
		list = new ArrayList<>();

		for (String str : strList) {
			list.add(fromString(str));
		}
	}

	public List<String> getConvertedList() {
		List<String> componentStrings = new ArrayList<>();

		for (T component : list) {
			componentStrings.add(toString(component));
		}

		return componentStrings;
	}

	/**
	 * Used to add a component to this component list
	 * 
	 * @param team the player's team
	 * @param component The component to add
	 */
	public void add(Team team, T component) {
		list.add(component);
	}
 
	/**
	 * Used to remove a component from this component list
	 * 
	 * @param team The playe's team
	 * @param component The component to remove
	 */
	public void remove(Team team, T component) {
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
	 * Clears all values from the list.
	 */
	public void clear() {
		list.clear();
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
