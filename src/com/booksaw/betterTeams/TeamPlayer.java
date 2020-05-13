package com.booksaw.betterTeams;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * This class is used to store all the information about a user in a team
 * 
 * @author booksaw
 *
 */
public class TeamPlayer {

	/**
	 * this is used to store the player that the object is associated with
	 */
	private UUID playerUUID;

	private PlayerRank rank;

	/**
	 * Used to create a new player
	 * 
	 * @param player the player that is associated with this object
	 * @param rank   the rank that the player has
	 */
	public TeamPlayer(OfflinePlayer player, PlayerRank rank) {
		this.playerUUID = player.getUniqueId();
		this.rank = rank;
	}

	/**
	 * Used to load player information relating to that player
	 * 
	 * @param data
	 */
	public TeamPlayer(String data) {
		String[] split = data.split(",");
		playerUUID = UUID.fromString(split[0]);
		rank = PlayerRank.valueOf(split[1]);
	}

	/**
	 * @return The rank of the player within their team
	 */
	public PlayerRank getRank() {
		return rank;
	}

	/**
	 * Set the rank of the player (used for promotions and demotions)
	 * 
	 * @param rank the rank of the player
	 */
	public void setRank(PlayerRank rank) {
		this.rank = rank;
	}

	/**
	 * @return The player which is associated with this object
	 */
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(playerUUID);
	}

	@Override
	public String toString() {
		return playerUUID + "," + rank;
	}

}
