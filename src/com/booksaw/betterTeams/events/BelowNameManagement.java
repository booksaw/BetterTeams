package com.booksaw.betterTeams.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;

import com.booksaw.betterTeams.Team;

public class BelowNameManagement implements Listener {

	Scoreboard board;

	public BelowNameManagement() {
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

		team.getScoreboardTeam(board).addEntry(player.getName());

	}

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e) {
		displayBelowName(e.getPlayer());
	}

}
