package com.booksaw.betterTeams.events;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.PromotePlayerEvent;
import com.booksaw.betterTeams.customEvents.post.PostDemotePlayerEvent;
import com.booksaw.betterTeams.customEvents.post.PostLevelupTeamEvent;
import com.booksaw.betterTeams.customEvents.post.PostPromotePlayerEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class RankupEvents implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onRankup(PostLevelupTeamEvent e) {
		List<String> endCommands = Main.plugin.getConfig()
				.getStringList("levels.l" + e.getCurrentLevel() + ".endCommands");
		runCommandList(endCommands, e.getTeam(), e.getCurrentLevel() + "", e.getCommandSender());

		List<String> startCommands = Main.plugin.getConfig()
				.getStringList("levels.l" + e.getNewLevel() + ".startCommands");
		runCommandList(startCommands, e.getTeam(), e.getNewLevel() + "", e.getCommandSender());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPromote(PostPromotePlayerEvent e) {
		List<String> commands = Main.plugin.getConfig().getStringList("promoteCommands." + e.getNewRank().toString().toUpperCase());
		runCommandList(commands, e.getTeam(), e.getNewRank().toString(), e.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onDemote(PostDemotePlayerEvent e) {
		List<String> commands = Main.plugin.getConfig().getStringList("demoteCommands." + e.getNewRank().toString().toUpperCase());
		runCommandList(commands, e.getTeam(), e.getNewRank().toString(), e.getPlayer());
	}

	private void runCommandList(List<String> commands, Team team, String level, OfflinePlayer source) {
		new BukkitRunnable() {

			@Override
			public void run() {
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
		}.runTask(Main.plugin);

	}

}
