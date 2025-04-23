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
import java.net.URI;
import java.net.URISyntaxException;
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
					// Create URL using the URI class which handles IDNs properly
					URI uri = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + ID);
					HttpsURLConnection connection = (HttpsURLConnection) uri.toURL().openConnection();
					connection.setRequestMethod("GET");
					try (BufferedReader reader = new BufferedReader(
							new InputStreamReader(connection.getInputStream()))) {
						UpdateChecker.this.spigotPluginVersion = reader.readLine();
					}
				} catch (IOException | URISyntaxException e) {
					Bukkit.getServer().getConsoleSender().sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&cUpdate checker failed! Disabling."));
					cancel();
					return;
				}

				if (isLatestVersion())
					return;

				Main.plugin.getLogger().warning(MessageManager.getMessage("admin.update"));

				latest = false;
				cancel();
			}
		}).runTaskTimerAsynchronously(this.javaPlugin, 0L, 10 * 60 * 20);
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
