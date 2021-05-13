package com.booksaw.betterTeams.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
	 * Sends the specified message to all team players stored in the list
	 * 
	 * @param message The message to send to all online players
	 */
	public void broadcastMessage(Message message) {
		for (Player p : getOnlinePlayers()) {
			message.sendMessage(p);
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

}
