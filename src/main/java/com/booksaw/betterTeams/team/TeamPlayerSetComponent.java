package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.message.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class TeamPlayerSetComponent extends SetTeamComponent<TeamPlayer> {

	/**
	 * @return A list of players which are currently online and on this team
	 */
	public List<Player> getOnlinePlayers() {
		List<Player> online = new ArrayList<>();

		for (TeamPlayer player : getClone()) {
			if (player.getPlayer().isOnline()) {
				online.add(player.getPlayer().getPlayer());
			}
		}

		return online;
	}

	/**
	 * @return A list of players which are currently online on this team
	 */
	public List<OfflinePlayer> getOfflinePlayers() {
		List<OfflinePlayer> offline = new ArrayList<>();

		for (TeamPlayer player : getClone()) {
			if (!player.getPlayer().isOnline()) {
				offline.add(player.getPlayer());
			}
		}

		return offline;
	}

	/**
	 * @return A list of all teamPlayers which are currently online
	 */
	public List<TeamPlayer> getOnlineTeamPlayers() {
		List<TeamPlayer> online = new ArrayList<>();

		for (TeamPlayer player : getClone()) {
			if (player.getPlayer().isOnline()) {
				online.add(player);
			}
		}

		return online;
	}

	/**
	 * Used to get the team player instance of that offline player
	 * 
	 * @param p The player to get the team player for
	 * @return The team player instance
	 */
	@Nullable
	public TeamPlayer getTeamPlayer(@NotNull OfflinePlayer p) {
		for (TeamPlayer player : getClone()) {
			if (p.getUniqueId().equals(player.getPlayer().getUniqueId())) {
				return player;
			}
		}
		return null;
	}

	public List<TeamPlayer> getRank(PlayerRank rank) {
		List<TeamPlayer> toReturn = new ArrayList<>();

		for (TeamPlayer player : getClone()) {
			if (player.getRank() == rank) {
				toReturn.add(player);
			}
		}

		return toReturn;
	}

	/**
	 * Sends the specified message to all team players stored in the list
	 * 
	 * @param message The message to send to all online players
	 */
	public void broadcastMessage(Message message) {
		for (Player p : getOnlinePlayers()) {
			message.sendMessage(p);
		}
	}

	/**
	 * Sends the specified title to all team players stored in the list
	 *
	 * @param message The message to send to all online players
	 */
	public void broadcastTitle(Message message) {
		for (Player p : getOnlinePlayers()) {
			message.sendTitle(p);
		}
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

	public boolean contains(OfflinePlayer player) {

		for (TeamPlayer tp : getClone()) {
			if (tp.getPlayer().getUniqueId().equals(player.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	private String getPlayersString(List<? extends OfflinePlayer> players) {
		final StringBuilder str = new StringBuilder();

		if (players.isEmpty()) {
			return "";
		}

		for (OfflinePlayer player : players) {
			str.append(player.getName()).append(", ");
		}

		return str.substring(0, str.length() - 2);
	}

	/**
	 * @return the string of all online players
	 */
	public String getOnlinePlayersString() {
		return getPlayersString(getOnlinePlayers());
	}

	public String getOfflinePlayersString() {
		return getPlayersString(getOfflinePlayers());
	}

}
