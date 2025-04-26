package com.booksaw.betterTeams;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * This class is used to store all the information about a user in a team
 *
 * @author booksaw
 */
public class TeamPlayer {

	/**
	 * this is used to store the player that the object is associated with
	 */
	@Getter
	private final UUID playerUUID;

	/**
	 * The rank of the player within their team
	 */
	@Setter
	@Getter
	private PlayerRank rank;

	/**
	 * This stores if the team is messaging to the team chat or the global chat
	 */
	@Setter
	private boolean teamChat = false;

	@Setter
	private boolean allyChat = false;

	/**
	 * Do not use this method (only should be used in the class Team, instead use
	 * Team.setTitle() as that will save the updated value)
	 */
	@Setter
	@Getter
	private String title;

	/*
	 * Whether or not this player accepts having their respawn location changed by team home anchor
	 * It is not recommended to use this value's setter without also changing and saving the anchored players
	 * of this player's team accordingly, otherwise the team will desync from this player around this vaule
	 * See Team.setPlayerAnchor(anchor)
	 */
	@Setter
	private boolean anchor = false;

	/**
	 * Used to create a new player
	 *
	 * @param player the player that is associated with this object
	 * @param rank   the rank that the player has
	 */
	public TeamPlayer(@NotNull OfflinePlayer player, @NotNull PlayerRank rank) {
		this.playerUUID = player.getUniqueId();
		this.rank = rank;
	}

	public TeamPlayer(OfflinePlayer player, PlayerRank rank, String title) {
		this.playerUUID = player.getUniqueId();
		this.rank = rank;
		this.title = title;
	}

	public TeamPlayer(OfflinePlayer player, PlayerRank rank, String title, boolean anchor) {
		this.playerUUID = player.getUniqueId();
		this.rank = rank;
		this.title = title;
		this.anchor = anchor;
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
	}

	/**
	 * @return The player which is associated with this object
	 */
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(playerUUID);
	}

	public Optional<Player> getOnlinePlayer() {
		return Optional.ofNullable(Bukkit.getPlayer(playerUUID));
	}

	@Override
	public String toString() {
		if (title == null || title.isEmpty()) {
			return playerUUID + "," + rank;
		}
		return playerUUID + "," + rank + "," + title;
	}

	public boolean isInTeamChat() {
		return teamChat;
	}

	public boolean isInAllyChat() {
		return allyChat;
	}

	public boolean isAnchored() {
		return anchor;
	}
	
	/**
	 * @param returnTo the chat color that should be returned to after the prefix
	 *                 has been added (to stop the color of the prefix continuing
	 *                 for the rest of the message)
	 * @return the prefix for messages that the player has sent
	 */
	public String getPrefix(ChatColor returnTo) {
		if (title == null || title.isEmpty()) {
			return rank.getPrefix();
		} else {
			return rank.getPrefix() + title + returnTo + " ";
		}
	}

	/**
	 * Checks if the player associated with this TeamPlayer is currently online.
	 *
	 * @return true if the player is online, false otherwise
	 */
	public boolean isOnline() {
		return getPlayer().isOnline();
	}
}
