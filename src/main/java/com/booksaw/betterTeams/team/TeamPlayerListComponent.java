package com.booksaw.betterTeams.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.message.Message;

public abstract class TeamPlayerListComponent extends ListTeamComponent<TeamPlayer> {

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

	/**
	 * @return the string of all online players
	 */
	public String getOnlinePlayersString() {

		String str = "";

		List<Player> onlinePlayers = getOnlinePlayers();

		if (onlinePlayers.size() == 0) {
			return "";
		}

		for (Player player : onlinePlayers) {
			str = str + player.getName() + ", ";
		}

		str = str.substring(0, str.length() - 2);

		return str;
	}

	public String getOfflinePlayersString() {
		String str = "";

		List<OfflinePlayer> offlinePlayers = getOfflinePlayers();

		if (offlinePlayers.size() == 0) {
			return "";
		}

		for (OfflinePlayer player : offlinePlayers) {
			str = str + player.getName() + ", ";
		}

		str = str.substring(0, str.length() - 2);

		return str;
	}

}
