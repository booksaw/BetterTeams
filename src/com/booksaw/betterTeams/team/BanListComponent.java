package com.booksaw.betterTeams.team;

import org.bukkit.OfflinePlayer;

public class BanListComponent extends UuidListComponent {

	@Override
	public String getSectionHeading() {
		return "bans";
	}

	/**
	 * Used to check if the uuid of the specified player is stored within the ban
	 * list
	 * 
	 * @param player The player to check
	 * @return If that player is banned or not
	 */
	public boolean contains(OfflinePlayer player) {
		return contains(player.getUniqueId());
	}

}
