package com.booksaw.betterTeams.team.storage.team;

/**
 * @author James
 *
 */
public enum StoredTeamValue {

	/**
	 * the name of the team
	 */
	NAME("name"),

	/**
	 * The tag of the team
	 */
	TAG("tag"),

	/**
	 * If the team is open or not
	 */
	OPEN("open", TeamStorageType.BOOLEAN),

	/**
	 * The description of the team
	 */
	DESCRIPTION("description"),

	/**
	 * The color the team has set
	 */
	COLOR("color"),

	/**
	 * The location of the team home (stored as a String)
	 */
	HOME("home"),

	/**
	 * The score of the team
	 */
	SCORE("score", TeamStorageType.INTEGER),

	/**
	 * The balance of the team
	 */
	MONEY("money", TeamStorageType.DOUBLE),

	/**
	 * The level of the team
	 */
	LEVEL("level", TeamStorageType.INTEGER),

	/**
	 * If the team has pvp enabled or not
	 */
	PVP("pvp", TeamStorageType.BOOLEAN);

	/**
	 * Used to store the reference that the value is saved by
	 */
	private final String reference;

	private final TeamStorageType storageType;

	StoredTeamValue(String reference) {
		this(reference, TeamStorageType.STRING);
	}

	StoredTeamValue(String reference, TeamStorageType storageType) {
		this.reference = reference;
		this.storageType = storageType;

	}

	/**
	 * @return The saving reference of this value
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @return The datatype of this variable
	 */
	public TeamStorageType getStorageType() {
		return storageType;
	}

}
