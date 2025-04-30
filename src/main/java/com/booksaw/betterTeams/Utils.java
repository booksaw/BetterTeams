package com.booksaw.betterTeams;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

	private Utils() {
		// stopping this class becoming an object
	}

	/**
	 * Used to get an offline player, unlike the inbuilt method this will return
	 * null if the player is invalid.
	 *
	 * @param name The name of the player
	 * @return The offlinePlayer object
	 */
	public static @Nullable OfflinePlayer getOfflinePlayer(String name) {
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);

		if (!player.hasPlayedBefore()) {
			for (Team team : Team.getTeamManager().getLoadedTeamListClone().values()) {
				for (OfflinePlayer offlinePlayer : team.getMembers().getOfflinePlayers()) {
					if (offlinePlayer.getName().equalsIgnoreCase(name)) {
						return offlinePlayer;
					}
				}
			}

			return null;
		}

		return player;
	}

	/**
	 * Serializes the contents of an inventory into a YAML string representation.
	 * Non-null items are stored with their position index as the key.
	 *
	 * @param inventory The inventory to serialize
	 * @return A YAML string containing the serialized inventory data
	 * @throws NullPointerException if inventory is null
	 */
	public static @NotNull String serializeInventory(@NotNull Inventory inventory) {
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

	/**
	 * Converts a single ItemStack into a YAML string representation.
	 *
	 * @param itemStack The ItemStack to serialize
	 * @return A YAML string containing the serialized item data
	 */
	public static @NotNull String dumpItem(ItemStack itemStack) {
		YamlConfiguration json = new YamlConfiguration();
		json.set("item", itemStack);
		return json.saveToString();
	}

	/**
	 * Deserializes inventory data from a YAML string and populates the provided inventory.
	 * Each item is placed at its original position in the inventory.
	 *
	 * @param inventory The inventory to populate with deserialized items
	 * @param jsons     The YAML string containing serialized inventory data
	 * @throws NullPointerException if inventory or jsons is null
	 */
	public static void deserializeIntoInventory(@NotNull Inventory inventory, @NotNull String jsons) {
		try {
			YamlConfiguration json = new YamlConfiguration();
			json.loadFromString(jsons);

			Map<String, Object> items = json.getConfigurationSection("items").getValues(false);
			for (Map.Entry<String, Object> item : items.entrySet()) {
				ItemStack itemstack = (ItemStack) item.getValue();
				int idx = Integer.parseInt(item.getKey());
				inventory.setItem(idx, itemstack);
			}
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if a player is currently in vanished state by examining their metadata.
	 *
	 * @param player The player to check for vanish status
	 * @return true if the player is vanished, false otherwise
	 * @throws NullPointerException if player is null
	 */
	public static boolean isVanished(final @NotNull Player player) {
		if (!player.isOnline())
			return false;

		final List<MetadataValue> values = player.getMetadata("vanished");

		for (final MetadataValue meta : values)
			if (meta.asBoolean())
				return true;

		return false;
	}

	public static @NotNull <T> List<T> filterNonNull(Collection<T> collection) {
		if (collection == null || collection.isEmpty()) return Collections.emptyList();
		return collection.stream()
				.filter(java.util.Objects::nonNull)
				.collect(Collectors.toList());
	}
}
