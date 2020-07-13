package com.booksaw.betterTeams.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.message.MessageManager;

public class AllyManagement implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Team team = Team.getTeam(e.getPlayer()); 
		if(team == null) {
			return;
		}
		
		if(team.getRequests().size() == 0) {
			return;
		}
		
		TeamPlayer player = team.getTeamPlayer(e.getPlayer()); 
		
		if(player.getRank() == PlayerRank.OWNER) {
			MessageManager.sendMessage(e.getPlayer(), "ally.onJoin");
		}
	}

}
