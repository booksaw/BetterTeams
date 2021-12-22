package com.booksaw.betterTeams;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utils {

	private Utils() {
		// stopping this class becomming an object
	}

	/**
	 * Used to get an offline player, unlike the inbuilt method this will return
	 * null if the player is invalid.
	 * 
	 * @param name The name of the player
	 * @return The offlinePlayer object
	 */
	public static OfflinePlayer getOfflinePlayer(String name) {

		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			// somehow the player name can be null in some circumstances
			if (player.getName() == null) {
				continue;
			}

			if (player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		}
		return null;

	}

	public static String serializeInventory(Inventory inventory) {
		YamlConfiguration json = new YamlConfiguration();
		int idx = 0;
		HashMap<String, ItemStack> items = new HashMap<>();
		for (ItemStack item : inventory.getContents()) {
			int i = idx++;
			if (item == null) {
				continue;
			}
			items.put("" + i, item);
		}
		json.createSection("items", items);
		return json.saveToString();
	}

	public static String dumpItem(ItemStack itemStack) {
		YamlConfiguration json = new YamlConfiguration();
		json.set("item", itemStack);
		return json.saveToString();
	}

	public static Inventory deserializeInventory(Inventory inventory, String jsons)
			throws InvalidConfigurationException {
		try {
			YamlConfiguration json = new YamlConfiguration();
			json.loadFromString(jsons);

			Map<String, Object> items = json.getConfigurationSection("items").getValues(false);
			for (Map.Entry<String, Object> item : items.entrySet()) {
				ItemStack itemstack = (ItemStack) item.getValue();
				int idx = Integer.parseInt(item.getKey());
				inventory.setItem(idx, itemstack);
			}
			return inventory;

		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

}
