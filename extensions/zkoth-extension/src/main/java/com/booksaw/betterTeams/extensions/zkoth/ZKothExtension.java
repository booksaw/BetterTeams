package com.booksaw.betterTeams.extensions.zkoth;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.extension.BetterTeamsExtension;
import org.bukkit.Bukkit;

public class ZKothExtension extends BetterTeamsExtension {

	private ZKothManager kothManager;

	@Override
	public void onEnable() {
		int points = getConfig().config.getInt("pointsPerCapture");

		this.kothManager = new ZKothManager(points);

		getPlugin().getServer().getPluginManager().registerEvents(kothManager, getPlugin());

		getLogger().info("ZKoth extension enabled.");
	}
}