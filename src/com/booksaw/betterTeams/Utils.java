package com.booksaw.betterTeams;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Utils {

	private Utils() {
		// stopping this class becomming an object
	}

	/**
	 * Used to get an offline player, unlike the inbuilt method this will return
	 * null if the player is invalid.
	 * 
	 * @param name The name of the player
	 * @return The offlinePlayer object
	 */
	public static OfflinePlayer getOfflinePlayer(String name) {

		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;

	}

}
