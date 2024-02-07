package com.booksaw.betterTeams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class ConfigManager {

	public final YamlConfiguration config;
	private final String resourceName;
	private final String filePath;

	public ConfigManager(String resourceName, boolean updateChecks) {
		if (!resourceName.endsWith(".yml")) {
			resourceName = resourceName + ".yml";
		}

		this.resourceName = resourceName;

		File f = new File(Main.plugin.getDataFolder() + File.separator + resourceName);
		this.filePath = f.getPath();

		if (!f.exists()) {
			Main.plugin.saveResource(resourceName, false);
		}

		config = YamlConfiguration.loadConfiguration(f);

		if (updateChecks) {
			updateFromDefaultSave();
		}

	}

	/**
	 * Used to load / create a config file where the resource name is different from
	 * the destination path
	 * 
	 * @param resourceName The name of the resource within the jar file
	 * @param filePath     The path to save the resource to
	 */
	public ConfigManager(String resourceName, String filePath) {
		if (!resourceName.endsWith(".yml")) {
			resourceName = resourceName + ".yml";
		}

		this.resourceName = resourceName;

		if (!filePath.endsWith(".yml")) {
			filePath = filePath + ".yml";
		}
		this.filePath = Main.plugin.getDataFolder().getPath() + File.separator + filePath;
		File f = new File(Main.plugin.getDataFolder(), filePath);

		if (!f.exists()) {
			saveResource(resourceName, this.filePath, false);
		}
		config = YamlConfiguration.loadConfiguration(f);

	}

	public void save() {
		save(true);
	}

	public void save(boolean log) {
		if (log) {
			Bukkit.getLogger().info("Saving new values to " + filePath);
		}

		File f = new File(filePath);
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
			logger.info("[BetterTeams] Checking if the file " + resourceName + " is up to date");
		}

		List<String> changes = updateFileConfig(Main.plugin.getResource(resourceName));
		int migratedKeys = updateVariables();

		if (log) {
			if (changes.isEmpty() && migratedKeys == 0) {
				logger.info("[BetterTeams] File is up to date");
			} else {
				if (!changes.isEmpty()) {
					logger.info("[BetterTeams] ==================================================================");
					logger.info("[BetterTeams] File is not updated, adding values under the following references:");

					for (String str : changes) {
						logger.info("[BetterTeams] - " + str);
					}
				}

				if (migratedKeys != 0) {
					logger.info("[BetterTeams] " + resourceName + " is using legacy variables. Migration taking place.");
					logger.info("[BetterTeams] " + migratedKeys + " references were migrated.");
				}

				logger.info("[BetterTeams] " + resourceName
						+ " is now updated to the latest version, thank you for using BetterTeams");
				logger.info("[BetterTeams] ==================================================================");

			}
		}

		if (!changes.isEmpty() || migratedKeys != 0) {
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

	private int updateVariables() {
		if (!config.saveToString().contains("%s")) {
			return 0;
		}
		int migratedKeys = 0;
		for (String key : config.getKeys(true)) {
			Object keyVal = config.get(key);
			if (keyVal instanceof String) {
				String stringVal = (String) keyVal;

				if (!stringVal.contains("%s")) {
					continue;
				}

				Pattern pattern = Pattern.compile("%s");
				Matcher matcher = pattern.matcher(stringVal);

				StringBuilder sb = new StringBuilder();
				int count = 0;
				while (matcher.find()) {
					matcher.appendReplacement(sb, "{" + count++ + "}");
				}
				matcher.appendTail(sb);

				config.set(key, sb.toString());
				migratedKeys++;
			}
		}
		return migratedKeys;
	}

	public void saveResource(String resourcePath, String resultPath, boolean replace) {
		if (resourcePath == null || resourcePath.equals(""))
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		if (resultPath == null || resultPath.equals(""))
			throw new IllegalArgumentException("ResultPath cannot be null or empty");

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = Main.plugin.getResource(resourcePath);

		if (in == null)
			throw new IllegalArgumentException(
					"The embedded resource '" + resourcePath + "' cannot be found in " + Main.plugin.getDataFolder());
		File outFile = new File(resultPath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = new File(resultPath.substring(0, (lastIndex >= 0) ? lastIndex : 0));

		if (!outDir.exists())
			outDir.mkdirs();

		try {
			if (!outFile.exists() || replace) {
				if (!outFile.exists()) {
					outFile.createNewFile();
				}

				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0)
					out.write(buf, 0, len);
				out.close();
				in.close();
			} else {
				Main.plugin.getLogger().log(Level.WARNING, "Could not save " + resourcePath + " to " + outFile
						+ " because " + outFile.getName() + " already exists.");
			}
		} catch (IOException ex) {
			Main.plugin.getLogger().log(Level.SEVERE, "Could not save " + resourcePath + " to " + resultPath, ex);
		}
	}

	public String getResourceName() {
		return resourceName;
	}

	public String getFilePath() {
		return filePath;
	}

}
