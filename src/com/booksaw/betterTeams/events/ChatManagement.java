package com.booksaw.betterTeams.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

import net.md_5.bungee.api.ChatColor;

public class ChatManagement implements Listener {

	private static boolean doPrefix;

	public static void enable() {
		doPrefix = Main.pl.getConfig().getBoolean("prefix");
	}

	/**
	 * This detects when the player speaks and either adds a prefix or puts the
	 * message in the team chat
	 * 
	 * @param event the chat event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onChat(AsyncPlayerChatEvent event) {

		if (event.isCancelled()) {
			return;
		}

		Player p = event.getPlayer();
		Team team = Team.getTeam(p);

		if (team == null) {
			return;
		}

		TeamPlayer teamPlayer = team.getTeamPlayer(p);
		if (!teamPlayer.isInTeamChat()) {
			if (doPrefix) {
				event.setFormat(ChatColor.AQUA + "[" + team.getName() + "] " + ChatColor.WHITE + event.getFormat());
			}

			return;
		}

		// player is sending to team chat
		event.setCancelled(true);

		team.sendMessage(teamPlayer, event.getMessage());
	}

}
