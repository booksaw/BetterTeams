package com.booksaw.betterTeams.extensions.zkoth;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.extension.BetterTeamsExtension;
import org.bukkit.Bukkit;

public class ZKothExtension extends BetterTeamsExtension {

	private ZKothManager kothManager;

	@Override
	public void onEnable() {
		// This loads the config.yml from this JAR's resources
		saveDefaultConfig();

		// Get the value from this extension's config
		int points = getConfig().getInt("pointsPerCapture");

		// Create and register the listener, passing the config value
		this.kothManager = new ZKothManager(points);

		// Register events using the main BetterTeams plugin instance
		getPlugin().getServer().getPluginManager().registerEvents(kothManager, getPlugin());

		getLogger().info("ZKoth extension enabled.");
	}
}