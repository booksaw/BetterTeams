package com.booksaw.betterTeams.team;

import java.util.*;

import com.booksaw.betterTeams.Team;

public abstract class SetTeamComponent<T> implements TeamComponent<Set<T>> {

	protected final Set<T> set;

	protected SetTeamComponent() {
		set = new HashSet<>();
	}

	@Override
	public Set<T> get() {
		return set;
	}

	/**
	 * Used to get a clone of the stored list, this can be used to avoid concurrent
	 * modification
	 * 
	 * @return A clone of the stored list
	 */
	public Set<T> getClone() {
		return new HashSet<>(set);
	}

	@Override
	public void set(Set<T> newSet) {
		this.set.clear();
		this.set.addAll(newSet);
	}

	/**
	 * @return The number of members stored within the list
	 */
	public int size() {
		return set.size();
	}

	/**
	 * @return If the list is empty
	 */
	public boolean isEmpty() {
		return set.isEmpty();
	}

	public void load(Iterable<String> strList) {
		set.clear();
		for (String str : strList) {
			set.add(fromString(str));
		}
	}

	public List<String> getConvertedList() {
		List<String> componentStrings = new ArrayList<>();

		for (T component : set) {
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
		set.add(component);
	}
 
	/**
	 * Used to remove a component from this component list
	 * 
	 * @param team The playe's team
	 * @param component The component to remove
	 */
	public void remove(Team team, T component) {
		set.remove(component);
	}

	/**
	 * Used to check if the component provided is stored within this component
	 * 
	 * @param component The component to check
	 * @return If the component is within this component list
	 */
	public boolean contains(T component) {
		return set.contains(component);
	}

	/**
	 * Clears all values from the list.
	 */
	public void clear() {
		set.clear();
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
