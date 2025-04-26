package com.booksaw.betterTeams.team.storage.team;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.Warp;

import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

/**
 * Used to manage the storage for a single team. All values in set using
 * {@code set(...)} will be automatically saved
 * <p>
 * <b>NOTE TO SUBCLASSES: Ensure you save any changes if they are not saved
 * automatically</b>
 * </p>
 *
 * @author booksaw
 */
public abstract class TeamStorage {

	protected final Team team;

	protected TeamStorage(Team team) {
		this.team = team;
	}

	/**
	 * Used to store the given information at the given location
	 *
	 * @param location The location to store the information
	 * @param value    The value to store
	 */
	public void set(StoredTeamValue location, Object value) {
		set(location.getReference(), location.getStorageType(), value);
	}

	/**
	 * Used to store the given information at the given location
	 * <p>
	 * If possible use a StoredTeamValue
	 * </p>
	 * <p>
	 * This will assume the value type is a string
	 * </p>
	 *
	 * @param location The location to store the information
	 * @param value    the value to store
	 */
	public void set(String location, Object value) {
		set(location, TeamStorageType.STRING, value);
	}

	/**
	 * Used to store the given information at the given location
	 *
	 * @param location    The location to store the information
	 * @param storageType The storage type of the information
	 * @param value       The value to store
	 */
	public void set(String location, TeamStorageType storageType, Object value) {

		// if the object is to be stored as a string, converting to a string and
		// bypassing checks
		if (storageType == TeamStorageType.STRING) {
			setValue(location, storageType, value.toString());
			return;
		}

		if (!storageType.getStorageClass().isInstance(value)) {
			throw new IllegalArgumentException(
					"Provided object was not of the correct type, this means that the object could not be saved. Expected type: "
							+ storageType);
		}

		setValue(location, storageType, value);
	}

	/**
	 * Used to set a value after checks have been made
	 *
	 * @param location    the location to store the value
	 * @param storageType The type of value that is being stored
	 * @param value       The value to store
	 */
	protected abstract void setValue(String location, TeamStorageType storageType, Object value);

	private void checkCorrectType(StoredTeamValue reference, TeamStorageType type) {
		if (reference.getStorageType() != type) {
			throw new IllegalArgumentException("Requested value is not of the valid type.");
		}
	}

	public String getString(StoredTeamValue reference) {
		checkCorrectType(reference, TeamStorageType.STRING);
		return getString(reference.getReference());
	}

	public abstract String getString(String reference);

	public boolean getBoolean(StoredTeamValue reference) {
		checkCorrectType(reference, TeamStorageType.BOOLEAN);
		return getBoolean(reference.getReference());
	}

	public abstract boolean getBoolean(String reference);

	public double getDouble(StoredTeamValue reference) {
		checkCorrectType(reference, TeamStorageType.DOUBLE);
		return getDouble(reference.getReference());
	}

	public abstract double getDouble(String reference);

	public int getInt(StoredTeamValue reference) {
		checkCorrectType(reference, TeamStorageType.INTEGER);
		return getInt(reference.getReference());
	}

	public abstract int getInt(String reference);

	public abstract List<TeamPlayer> getPlayerList();

	public abstract void setPlayerList(List<String> players);

	public abstract List<UUID> getAnchoredPlayerList();

	public abstract void setAnchoredPlayerList(List<String> players);

	public abstract void setAnchor(TeamPlayer player, boolean anchor);

	public abstract List<String> getBanList();

	public abstract void setBanList(List<String> players);

	public abstract List<String> getAllyList();

	public abstract void setAllyList(List<String> players);

	public abstract List<String> getAllyRequestList();

	public abstract void setAllyRequestList(List<String> players);

	public abstract void getEchestContents(Inventory inventory);

	public abstract void setEchestContents(Inventory inventory);

	public abstract List<String> getWarps();

	public abstract void setWarps(List<String> warps);

	public abstract List<String> getClaimedChests();

	public abstract void setClaimedChests(List<String> chests);

	public abstract void addBan(UUID component);

	public abstract void removeBan(UUID component);

	public abstract void addAlly(UUID ally);

	public abstract void removeAlly(UUID ally);

	public abstract void addAllyRequest(UUID requesting);

	public abstract void removeAllyRequest(UUID requesting);

	public abstract void addWarp(Warp component);

	public abstract void removeWarp(Warp component);

	public abstract void promotePlayer(TeamPlayer promotePlayer);

	public abstract void demotePlayer(TeamPlayer demotePlayer);

	public abstract void setTitle(TeamPlayer player);
}
