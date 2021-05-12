package com.booksaw.betterTeams.cost;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Main;

/**
 * Used to track how long of a cost is required for a command
 * 
 * @author booksaw
 *
 */
public class CostManager {

	public static boolean costFromTeam;

	final HashMap<String, CommandCost> prices;

	public CostManager(String command) {
		File f = new File("plugins/BetterTeams/" + command + ".yml");

		if (!f.exists()) {
			Main.plugin.saveResource(command + ".yml", false);
		}

		YamlConfiguration cooldown = YamlConfiguration.loadConfiguration(f);

		if (Main.plugin.getConfig().getInt("version") <= 5) {
			cooldown.set("costFromTeam", true);
			try {
				cooldown.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			costFromTeam = true;
		} else {
			costFromTeam = cooldown.getBoolean("costFromTeam");
		}

		prices = new HashMap<>();

		List<String> cooldownsStr = cooldown.getStringList("costs");
		for (String temp : cooldownsStr) {
			try {
				String[] split = temp.split(":");
				prices.put(split[0], new CommandCost(split[0], Double.parseDouble(split[1])));
			} catch (Exception e) {
				Bukkit.getLogger().info("Something went wrong while enabling a cost, there appears to be an error with "
						+ command + ".yml. (ERROR: " + e.getMessage() + ")");

			}
		}

	}

	public CommandCost getCommandCost(String command) {
		if (prices.containsKey(command)) {
			return prices.get(command);
		}
		return new NoCost();
	}

	/**
	 * This class is returned when a command does not have a cost, this is used to
	 * avoid running many null checks throughout the program. The class is used to
	 * stop any cost tracking for a specific command
	 * 
	 * @author booksaw
	 *
	 */
	public static class NoCost extends CommandCost {

		public NoCost() {
			super("", 0);
		}

		@Override
		public boolean runCommand(Player player) {
			return true;
		}

		@Override
		public boolean hasBalance(Player player) {
			return true;
		}

	}

}
