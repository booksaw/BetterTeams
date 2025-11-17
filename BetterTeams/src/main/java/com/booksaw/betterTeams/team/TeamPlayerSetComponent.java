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
				.filter(p -> !p.isOnline())
				.collect(Collectors.toList());
	}

	/**
	 * @return A list of all teamPlayers which are currently online
	 */
	public List<TeamPlayer> getOnlineTeamPlayers() {
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
	public void broadcastMessage(@NotNull Message message) {
		message.sendMessage(getOnlinePlayers());
	}

	/**
	 * Sends the specified title to all team players stored in the list
	 *
	 * @param message The message to send to all online players
	 */
	public void broadcastTitle(@NotNull Message message) {
		message.sendTitle(getOnlinePlayers());
	}

	@Override
	public TeamPlayer fromString(String str) {
		return new TeamPlayer(str);
	}

	@Override
	public String toString(@NotNull TeamPlayer component) {
		return component.toString();
	}

	@Override
	public boolean contains(@NotNull TeamPlayer component) {
		return contains(component.getPlayer());
	}

	/**
	 * Checks if the given player is in this team
	 *
	 * @param player The player to check for
	 * @return true if the player is in this team, false otherwise
	 */
	public boolean contains(OfflinePlayer player) {
		return getTeamPlayer(player) != null;
	}

	private String getPlayersString(@NotNull List<? extends OfflinePlayer> players) {
		return players.stream().map(p -> p.getName()).collect(Collectors.joining(", "));
	}

	/**
	 * @return A comma-separated string of all online player names
	 */
	public String getOnlinePlayersString() {
		return getPlayersString(getOnlinePlayers());
	}

	/**
	 * @return A comma-separated string of all offline player names
	 */
	public String getOfflinePlayersString() {
		return getPlayersString(getOfflinePlayers());
	}
}