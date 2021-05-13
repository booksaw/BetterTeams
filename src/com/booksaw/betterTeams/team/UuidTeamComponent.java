package com.booksaw.betterTeams.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

public abstract class UuidTeamComponent implements TeamComponent<List<UUID>> {

	protected List<UUID> uuids;

	protected UuidTeamComponent() {
		uuids = new ArrayList<>();
	}

	@Override
	public List<UUID> get() {
		return uuids;
	}

	@Override
	public void set(List<UUID> uuids) {
		this.uuids = uuids;
	}

	@Override
	public void load(ConfigurationSection section) {
		List<String> uuidStrings = section.getStringList(getSectionHeading());
		uuids = new ArrayList<>();

		for (String str : uuidStrings) {
			uuids.add(UUID.fromString(str));
		}
	}

	@Override
	public void save(ConfigurationSection section) {
		List<String> uuidStrings = new ArrayList<>();

		for (UUID uuid : uuids) {
			uuidStrings.add(uuid.toString());
		}

		section.set(getSectionHeading(), uuidStrings);
	}

	/**
	 * Used to add a UUID to this component
	 * 
	 * @param uuid The UUID to add
	 */
	public void add(UUID uuid) {
		uuids.add(uuid);
	}

	/**
	 * Used to remove a UUID from this component
	 * 
	 * @param uuid The UUID to remove
	 */
	public void remove(UUID uuid) {
		uuids.remove(uuid);
	}

	/**
	 * Used to check if the UUID provided is stored within this component
	 * 
	 * @param uuid The UUID to check
	 * @return If the UUID is within this component
	 */
	public boolean contains(UUID uuid) {
		return uuids.contains(uuid);
	}

	/**
	 * @return The reference within the team storage where the data is stored
	 */
	public abstract String getSectionHeading();

}
