package com.booksaw.betterTeams;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class ConfigManager {

	public final YamlConfiguration config;
	private final String configName;

	public ConfigManager(String configName, boolean updateChecks) {
		this.configName = configName;
		File f = new File(Main.plugin.getDataFolder() + File.separator + configName + ".yml");

		if (!f.exists()) {
			Main.plugin.saveResource(configName + ".yml", false);
		}

		config = YamlConfiguration.loadConfiguration(f);

		if (updateChecks) {
			updateFromDefaultSave();
		}

	}

	public void save() {
		save(true);
	}

	public void save(boolean log) {
		if (log) {
			Bukkit.getLogger().info("Saving new values to " + configName + ".yml");
		}
		File f = new File(Main.plugin.getDataFolder() + File.separator + configName + ".yml");
		try {
			config.save(f);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
		}
	}

	public void updateFromDefaultSave() {
		updateFromDefaultSave(true);
	}

	public void updateFromDefaultSave(boolean log) {

		Logger logger = Bukkit.getLogger();

		if (log) {
			logger.info("[BetterTeams] Checking if the file " + configName + " is up to date");
		}

		List<String> changes = updateFileConfig(Main.plugin.getResource(configName + ".yml"));

		if (log) {
			if (changes.isEmpty()) {
				logger.info("[BetterTeams] File is up to date");
			} else {
				logger.info("[BetterTeams] ==================================================================");
				logger.info("[BetterTeams] File is not updated, adding values under the following references:");

				for (String str : changes) {
					logger.info("[BetterTeams] - " + str);
				}

				logger.info("[BetterTeams] " + configName
						+ " is now upated to the latest version, thank you for using BetterTeams");
				logger.info("[BetterTeams] ==================================================================");

			}
		}

		if (!changes.isEmpty()) {
			save(false);
		}

	}

	private @NotNull List<String> updateFileConfig(@NotNull InputStream input) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		return updateFileConfig(reader);
	}

	private @NotNull List<String> updateFileConfig(@NotNull BufferedReader reader) {

		List<String> addedPaths = new ArrayList<>();

		YamlConfiguration internalConfig = YamlConfiguration.loadConfiguration(reader);
		for (String str : internalConfig.getKeys(true)) {
			if (!config.contains(str)) {
				Object toSave = internalConfig.get(str);
				if (toSave == null || toSave instanceof ConfigurationSection) {
					continue;
				}

				// saving the new value to the config
				config.set(str, toSave);
				addedPaths.add(str);
			}
		}

		return addedPaths;

	}
}
