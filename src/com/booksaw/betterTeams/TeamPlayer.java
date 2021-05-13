package com.booksaw.betterTeams;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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

	/**
	 * This stores the players rank within the team
	 */
	private PlayerRank rank;

	/**
	 * This stores if the team is messaging to the team chat or the global chat
	 */
	private boolean teamChat = false;

	private boolean allyChat = false;

	private String title;

	/**
	 * Used to create a new player
	 * 
	 * @param player the player that is associated with this object
	 * @param rank   the rank that the player has
	 */
	public TeamPlayer(@NotNull OfflinePlayer player, @NotNull PlayerRank rank) {
		this.playerUUID = player.getUniqueId();
		this.rank = rank;
		teamChat = false;
	}

	/**
	 * Used to load player information relating to that player
	 * 
	 * @param data the data for the player (uuid,rank,title)
	 */
	public TeamPlayer(@NotNull String data) {
		String[] split = data.split(",");
		playerUUID = UUID.fromString(split[0]);
		rank = PlayerRank.valueOf(split[1]);
		if (split.length > 2) {
			title = split[2];
		}
		teamChat = false;
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
	@NotNull
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(playerUUID);
	}

	@Override
	public String toString() {
		if (title == null || title.equals("")) {
			return playerUUID + "," + rank;
		}
		return playerUUID + "," + rank + "," + title;
	}

	public boolean isInTeamChat() {
		return teamChat;
	}

	public void setTeamChat(boolean teamChat) {
		this.teamChat = teamChat;
	}

	public boolean isInAllyChat() {
		return allyChat;
	}

	public void setAllyChat(boolean AllyChat) {
		this.allyChat = AllyChat;
	}

	/**
	 * @param returnTo the chat color that should be returned to after the prefix
	 *                 has been added (to stop the color of the prefix continuing
	 *                 for the rest of the message)
	 * @return the prefix for messages that the player has sent
	 */
	public String getPrefix() {
		if (title == null || title.equals("")) {
			return rank.getPrefix();
		} else {
			return rank.getPrefix() + title + " ";
		}
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Do not use this method (only should be used in the class Team, instead use
	 * Team.setTitle() as that will save the updated value)
	 * 
	 * @see com.booksaw.betterTeams.Team#setTitle
	 * @param title the new title for that player
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
