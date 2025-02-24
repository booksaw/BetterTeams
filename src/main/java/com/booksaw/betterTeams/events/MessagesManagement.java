package com.booksaw.betterTeams.events;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.customEvents.post.PostDisbandTeamEvent;
import com.booksaw.betterTeams.customEvents.post.PostPlayerJoinTeamEvent;
import com.booksaw.betterTeams.customEvents.post.PostPlayerLeaveTeamEvent;
import com.booksaw.betterTeams.message.Message;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MessagesManagement implements Listener {
	@EventHandler
	public void onPostPlayerJoinTeamEvent(PostPlayerJoinTeamEvent e) {
		if (Main.plugin.getConfig().getBoolean("announceTeamJoin")) {
			Message message = new ReferencedFormatMessage("announce.join", e.getPlayer().getName(), e.getTeam().getColor() + e.getTeam().getName() + ChatColor.RESET);
			for (Player player : Bukkit.getOnlinePlayers()) {
				message.sendMessage(player);
			}
		}
	}

	@EventHandler
	public void onPostDisbandTeamEvent(PostDisbandTeamEvent e) {
		if (Main.plugin.getConfig().getBoolean("announceTeamDisband")) {
			Message message = new ReferencedFormatMessage("announce.disband", e.getTeam().getColor() + e.getTeam().getName() + ChatColor.RESET);
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				message.sendMessage(onlinePlayer);
			}
		}
	}

	@EventHandler
	public void onPostPlayerLeaveTeamEvent(PostPlayerLeaveTeamEvent e) {
		if (Main.plugin.getConfig().getBoolean("announceTeamLeave")) {
			Message message = new ReferencedFormatMessage("announce.leave", e.getPlayer().getName(), e.getTeam().getColor() + e.getTeam().getName() + ChatColor.RESET);
			for (Player player : Bukkit.getOnlinePlayers()) {
				message.sendMessage(player);
			}
		}
	}
}
