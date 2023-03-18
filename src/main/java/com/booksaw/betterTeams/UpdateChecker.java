package com.booksaw.betterTeams;

import com.booksaw.betterTeams.message.MessageManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.IntStream;

public class UpdateChecker implements Listener {
	private static final int ID = 17129;

	private final JavaPlugin javaPlugin;

	private final String localPluginVersion;

	private String spigotPluginVersion;
	private boolean latest = true;

	public UpdateChecker(JavaPlugin javaPlugin) {
		this.javaPlugin = javaPlugin;
		this.localPluginVersion = javaPlugin.getDescription().getVersion();
		checkForUpdate();
	}

	public void checkForUpdate() {
		(new BukkitRunnable() {
			@Override
			public void run() {
				try {
					HttpsURLConnection connection = (HttpsURLConnection) (new URL(
							"https://api.spigotmc.org/legacy/update.php?resource=" + ID)).openConnection();
					connection.setRequestMethod("GET");
					UpdateChecker.this.spigotPluginVersion = (new BufferedReader(
							new InputStreamReader(connection.getInputStream()))).readLine();
				} catch (IOException e) {
					Bukkit.getServer().getConsoleSender().sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&cUpdate checker failed! Disabling."));
					cancel();
					return;
				}
				if (isLatestVersion())
					return;
				Bukkit.getLogger().warning(
						"[BetterTeams] An update for BetterTeams has been released. Update here (https://www.spigotmc.org/resources/better-teams.17129/)");
				latest = false;
				cancel();
			}
		}).runTaskTimerAsynchronously(this.javaPlugin, 0L, 12000L);
	}

	private boolean isLatestVersion() {
		try {
			int[] local = Arrays.stream(this.localPluginVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
			int[] spigot = Arrays.stream(this.spigotPluginVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
			return IntStream.range(0, local.length).filter(i -> (local[i] != spigot[i])).limit(1L)
					.mapToObj(i -> (local[i] >= spigot[i])).findFirst().orElse(Boolean.TRUE);
		} catch (NumberFormatException ignored) {
			return this.localPluginVersion.equals(this.spigotPluginVersion);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent p) {
		if (latest || !p.getPlayer().hasPermission("betterteams.admin")) {
			return;
		}

		MessageManager.sendMessage(p.getPlayer(), "admin.update");
	}
}
