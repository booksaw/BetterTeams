package com.booksaw.betterTeams.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;

import com.booksaw.betterTeams.Team;

public class BelowNameManagement implements Listener {

	BelowNameType type;

	Scoreboard board;

	public BelowNameManagement(BelowNameType type) {
		this.type = type;

		if (Bukkit.getScoreboardManager().getMainScoreboard() != null) {
			board = Bukkit.getScoreboardManager().getMainScoreboard();
		} else {
			board = Bukkit.getScoreboardManager().getNewScoreboard();
		}

	}

	public void displayBelowNameForAll() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			displayBelowName(p);
		}
	}

	public void displayBelowName(Player player) {
		player.setScoreboard(board);

		Team team = Team.getTeam(player);
		if (team == null) {
			return;
		}

		// checking the player has the correct permission node
		if (!player.hasPermission("betterTeams.teamName")) {
			// player does not have permission to have their team name displayed.
			return;
		}

		team.getScoreboardTeam(board, type).addEntry(player.getName());

	}

	/**
	 * Used when the plugin is disabled
	 */
	public void removeAll() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			remove(p);
		}
	}

	/**
	 * Used to remove the prefix / suffix from the specified player
	 * 
	 * @param player
	 */
	public void remove(Player player) {

		Team team = Team.getTeam(player);
		if (team == null) {
			return;
		}

		team.getScoreboardTeam(board, type).removeEntry(player.getName());

	}

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e) {
		displayBelowName(e.getPlayer());
	}

	public enum BelowNameType {
		PREFIX, SUFFIX, FALSE;

		public static BelowNameType getType(String string) {

			switch (string.toLowerCase()) {
			case "prefix":
			case "true":
				return PREFIX;
			case "suffix":
				return SUFFIX;
			}

			return FALSE;
		}
	}
}
