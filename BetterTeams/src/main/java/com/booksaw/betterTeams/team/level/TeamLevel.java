package com.booksaw.betterTeams.team.level;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specific level of a team.
 * This object holds all configuration data for a level.
 */
@Getter
@RequiredArgsConstructor
public class TeamLevel {

	private final int level;

	// --- Limits & Features ---
	private final int teamLimit;
	private final int maxChests;
	private final int maxWarps;
	private final double maxBalance;
	private final int maxAdmins;
	private final int maxOwners;

	// Upgrades
	/**
	 * The raw price string from config (e.g., "100s" for score, "500m" for money).
	 * @see #getCostValue()
	 */
	private final String price;

	// Commands
	private final List<String> startCommands;
	private final List<String> endCommands;

	private final List<String> rankLore;


	public double getCostValue() {
		if (price == null || price.isEmpty()) return 0;
		try {
			String number = price.substring(0, price.length() - 1);
			return Double.parseDouble(number);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Checks if the cost is in 'Score' points.
	 * @return true if price ends with 's'.
	 */
	public boolean isScoreCost() {
		return price != null && price.toLowerCase().endsWith("s");
	}

	/**
	 * Checks if the cost is in 'Money' (Vault economy).
	 * @return true if price ends with 'm'.
	 */
	public boolean isMoneyCost() {
		return price != null && price.toLowerCase().endsWith("m");
	}

	/**
	 * Returns the lore with translated color codes.
	 * @return List of colored strings.
	 */
	public List<String> getColoredLore() {
		List<String> colored = new ArrayList<>();
		if (rankLore != null) {
			for (String line : rankLore) {
				colored.add(ChatColor.translateAlternateColorCodes('&', line));
			}
		}
		return colored;
	}
}
