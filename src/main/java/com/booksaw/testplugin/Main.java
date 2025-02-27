package com.booksaw.testplugin;

import com.booksaw.betterTeams.Team;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onJoin(AsyncPlayerChatEvent e){
		Team team = Team.getTeam(e.getPlayer());

		List<String> playerNames = new ArrayList<>();
		if (team != null) {
			team.getMembers().getOnlinePlayers().iterator().forEachRemaining(player -> {
				playerNames.add(player.getName());
			});
		}
		e.getPlayer().sendMessage("Player names: " + playerNames.toString());

	}

}