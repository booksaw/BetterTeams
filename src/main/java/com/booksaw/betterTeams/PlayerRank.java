package com.booksaw.betterTeams;

import com.booksaw.betterTeams.message.MessageManager;

/**
 * This class is used to define all the possible ranks that a player can be
 * within a team
 *
 * @author booksaw
 */
public enum PlayerRank {
	/**
	 * The starting rank with the lowest number of permissions
	 */
	DEFAULT(0),

	/**
	 * The highest rank, this rank has full permissions over the team
	 */
	OWNER(2),

	/**
	 * This player has permissions to invite new players to the team and kick
	 * default users, but cannot ban people or change team settings
	 */
	ADMIN(1);

	public final int value;

	PlayerRank(int value) {
		this.value = value;
	}

	public static PlayerRank getRank(String string) {

		switch (string.toUpperCase()) {
		case "DEFAULT":
			return DEFAULT;
		case "OWNER":
			return OWNER;
		case "ADMIN":
			return ADMIN;
		default:
			return null;
		}

	}

	/**
	 * @return the prefix for that player rank
	 */
	public String getPrefix() {
		return MessageManager.getMessage("prefix." + this.toString().toLowerCase());
	}

}
