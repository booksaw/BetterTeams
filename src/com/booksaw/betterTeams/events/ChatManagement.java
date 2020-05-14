package com.booksaw.betterTeams.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

public class ChatManagement implements Listener {

	/**
	 * This detects when the player speaks it is important to have a high priority
	 * so before any processing is carried out, the event can be cancelled if needed
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGH)
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
			return;
		}

		// player is sending to team chat
		event.setCancelled(true);

		team.sendMessage(teamPlayer, event.getMessage());
	}

}
