package com.booksaw.betterTeams.events;

import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.DemotePlayerEvent;
import com.booksaw.betterTeams.customEvents.LevelupTeamEvent;
import com.booksaw.betterTeams.customEvents.PromotePlayerEvent;

import me.clip.placeholderapi.PlaceholderAPI;

public class RankupEvents implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onRankup(LevelupTeamEvent e) {
		List<String> endCommands = Main.plugin.getConfig()
				.getStringList("levels.l" + e.getCurrentLevel() + ".endCommands");
		runCommandList(endCommands, e.getTeam(), e.getCurrentLevel() + "", e.getCommandSender());

		List<String> startCommands = Main.plugin.getConfig()
				.getStringList("levels.l" + e.getNewLevel() + ".startCommands");
		runCommandList(startCommands, e.getTeam(), e.getNewLevel() + "", e.getCommandSender());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPromote(PromotePlayerEvent e) {
		List<String> commands = Main.plugin.getConfig().getStringList("promoteCommands");
		runCommandList(commands, e.getTeam(), e.getNewRank().toString(), e.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onDemote(DemotePlayerEvent e) {
		List<String> commands = Main.plugin.getConfig().getStringList("demoteCommands." + e.getNewRank().toString());
		runCommandList(commands, e.getTeam(), e.getNewRank().toString(), e.getPlayer());
	}

	private void runCommandList(List<String> commands, Team team, String level, OfflinePlayer source) {
		for (String str : commands) {
			str = str.replace("%team%", team.getName());
			str = str.replace("%level%", level);
			if (str.contains("%player%")) {

				for (TeamPlayer p : team.getMembers().getClone()) {
					String newStr = str.replace("%player%", Objects.requireNonNull(p.getPlayer().getName()));
					if (Main.placeholderAPI) {
						newStr = PlaceholderAPI.setPlaceholders(p.getPlayer(), newStr);
					}

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), newStr);
				}
			} else {
				if (Main.placeholderAPI) {
					str = PlaceholderAPI.setPlaceholders(source, str);
				}

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str);

			}
		}
	}

}
