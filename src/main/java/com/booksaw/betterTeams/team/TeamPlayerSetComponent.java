package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.message.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public abstract class TeamPlayerSetComponent extends SetTeamComponent<TeamPlayer> {

	/**
	 * @return A list of players which are currently online and on this team
	 */
	public List<Player> getOnlinePlayers() {
		return getClone().stream()
			.map(TeamPlayer::getPlayer)
			.filter(OfflinePlayer::isOnline)
			.map(OfflinePlayer::getPlayer)
			.collect(Collectors.toList());
	}

	/**
	 * @return A list of players which are currently online on this team
	 */
	public List<OfflinePlayer> getOfflinePlayers() {
		return getClone().stream()
			.map(TeamPlayer::getPlayer)
			.filter(OfflinePlayer::isOnline)
			.collect(Collectors.toList());
	}

	/**
	 * @return A list of all teamPlayers which are currently online
	 */
	public List<TeamPlayer> getOnlineTeamPlayers() {
		// Get all team players, filter for those who are online
		return getClone().stream()
			.filter(TeamPlayer::isOnline)
			.collect(Collectors.toList());
	}

	/**
	 * Used to get the team player instance of that offline player
	 *
	 * @param p The player to get the team player for
	 * @return The team player instance, or null if not found
	 */
	@Nullable
	public TeamPlayer getTeamPlayer(@NotNull OfflinePlayer p) {
		// Find the first team player whose UUID matches the given player
		return getClone().stream()
			.filter(teamPlayer -> p.getUniqueId().equals(teamPlayer.getPlayer().getUniqueId()))
			.findFirst()
			.orElse(null);
	}

	/**
	 * Get all team players with the specified rank
	 *
	 * @param rank The rank to filter by
	 * @return List of team players with the specified rank
	 */
	public List<TeamPlayer> getRank(PlayerRank rank) {
		return getClone().stream()
			.filter(player -> player.getRank() == rank)
			.collect(Collectors.toList());
	}

	/**
	 * Sends the specified message to all team players stored in the list
	 *
	 * @param message The message to send to all online players
	 */
	public void broadcastMessage(Message message) {
		getOnlinePlayers().forEach(message::sendMessage);
	}

	/**
	 * Sends the specified title to all team players stored in the list
	 *
	 * @param message The message to send to all online players
	 */
	public void broadcastTitle(Message message) {
		getOnlinePlayers().forEach(message::sendTitle);
	}

	@Override
	public TeamPlayer fromString(String str) {
		return new TeamPlayer(str);
	}

	@Override
	public String toString(TeamPlayer component) {
		return component.toString();
	}

	@Override
	public boolean contains(TeamPlayer component) {
		return contains(component.getPlayer());
	}

	/**
	 * Checks if the given player is in this team
	 *
	 * @param player The player to check for
	 * @return true if the player is in this team, false otherwise
	 */
	public boolean contains(OfflinePlayer player) {
		// Check if any team player has the same UUID as the given player
		return getClone().stream()
			.map(TeamPlayer::getPlayer)
			.map(OfflinePlayer::getUniqueId)
			.anyMatch(uuid -> uuid.equals(player.getUniqueId()));
	}

	/**
	 * @return A comma-separated string of all online player names
	 */
	public String getOnlinePlayersString() {
		// Create a comma-separated list of player names
		return getOnlinePlayers().stream()
			.map(Player::getName)
			.collect(Collectors.joining(", "));
	}

	/**
	 * @return A comma-separated string of all offline player names
	 */
	public String getOfflinePlayersString() {
		// Create a comma-separated list of player names
		return getOfflinePlayers().stream()
			.map(OfflinePlayer::getName)
			.collect(Collectors.joining(", "));
	}
}