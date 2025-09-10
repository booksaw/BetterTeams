package com.booksaw.betterTeams.team.meta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * This class manages custom metaData for a team.
 * It stores various properties as key-value pairs.
 */
public class TeamMeta {

	private final Map<String, String> metaData;

	public TeamMeta() {
		this.metaData = new HashMap<>();
	}

	/**
	 * Adds or updates a metaData entry.
	 * @param key   The identifier for the metaData.
	 * @param value The string value of the metaData.
	 */
	public void set(String key, String value) {
		this.metaData.put(key, value);
	}

	/**
	 * Retrieves a metaData value by its key.
	 * @param key The key of the metaData to retrieve.
	 * @return An Optional containing the value, or empty if not found.
	 */
	public Optional<String> get(String key) {
		return Optional.ofNullable(this.metaData.get(key));
	}

	/**
	 * Checks if a metaData entry with the specified key exists.
	 * @param key The key to check.
	 * @return True if the metaData exists, false otherwise.
	 */
	public boolean has(String key) {
		return this.metaData.containsKey(key);
	}

	/**
	 * Removes a metaData entry by its key.
	 * @param key The key of the metaData to remove.
	 */
	public void remove(String key) {
		this.metaData.remove(key);
	}

	/**
	 * Returns an unmodifiable view of all metaData entries.
	 * @return An unmodifiable Map of all metaData.
	 */
	public Map<String, String> getAll() {
		return Collections.unmodifiableMap(this.metaData);
	}

	/**
	 * Prepares the metadata for storage/serialization.
	 * @return A new Map<String, String> containing all metadata in a raw, serializable format.
	 */
	public Map<String, String> getSerialized() {
		return new HashMap<>(this.metaData);
	}

	/**
	 * Loads metaData from a raw map.
	 * @param rawMeta The map of raw metaData to load.
	 */
	public void load(Map<String, String> rawMeta) {
		this.metaData.clear();
		if (rawMeta != null) {
			this.metaData.putAll(rawMeta);
		}

	}
}
