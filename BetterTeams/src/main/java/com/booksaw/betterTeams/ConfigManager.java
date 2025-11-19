package com.booksaw.betterTeams;

import com.booksaw.betterTeams.extension.BetterTeamsExtension;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ConfigManager {

	public final YamlConfiguration config;
	private final String resourceName;
	private final String filePath;

	private final BetterTeamsExtension extension;

	public ConfigManager(String resourceName, boolean updateChecks) {
		this(resourceName, updateChecks, null);
	}

	public ConfigManager(BetterTeamsExtension extension, String resourceName, boolean updateChecks) {
		this(resourceName, updateChecks, extension);
	}


	private ConfigManager(String resourceName, boolean updateChecks, BetterTeamsExtension extension) {
		this.extension = extension;

		if (!resourceName.endsWith(".yml")) {
			resourceName = resourceName + ".yml";
		}

		this.resourceName = resourceName;

		File folder = (extension != null) ? extension.getDataFolder() : Main.plugin.getDataFolder();

		File f = new File(folder, resourceName);
		this.filePath = f.getPath();

		if (!f.exists()) {
			saveResource(resourceName, this.filePath, false);
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
		this.extension = null;
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
			log(Level.INFO, "Saving new values to " + filePath);
		}

		File f = new File(filePath);
		try {
			config.save(f);
		} catch (IOException ex) {
			log(Level.SEVERE, "Could not save config to " + f, ex);
		}
	}

	public void updateFromDefaultSave() {
		updateFromDefaultSave(true);
	}

	public void updateFromDefaultSave(boolean log) {

		if (log) {
			log(Level.INFO, "Checking if the file " + resourceName + " is up to date");
		}

		InputStream resourceStream = (extension != null)
				? extension.getResource(resourceName)
				: Main.plugin.getResource(resourceName);

		List<String> changes = updateFileConfig(resourceStream);
		boolean migratedVariables = migrateVariables(log);

		if (log) {
			if (changes.isEmpty() && !migratedVariables) {
				log(Level.INFO, "File is up to date");
			}
			if (!changes.isEmpty()) {
				log(Level.INFO, "==================================================================");
				log(Level.INFO, "File is not updated, adding values under the following references:");

				for (String str : changes) {
					log(Level.INFO, "- " + str);
				}

				log(Level.INFO, resourceName + " is now updated to the latest version, thank you for using BetterTeams");
				log(Level.INFO, "==================================================================");

			}
		}

		if (!changes.isEmpty() || migratedVariables) {
			save(false);
		}

	}

	private @NotNull List<String> updateFileConfig(@Nullable InputStream input) {
		if (input == null) {
			return new ArrayList<>();
		}

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

	private boolean migrateVariables(boolean log) {
		if (!config.saveToString().contains("%s")) {
			return false;
		}
		if (log) {
			log(Level.INFO, resourceName + " is using legacy variables. Migration taking place.");
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

				StringBuffer sb = new StringBuffer();
				int count = 0;
				while (matcher.find()) {
					matcher.appendReplacement(sb, "{" + count++ + "}");
				}
				matcher.appendTail(sb);

				config.set(key, sb.toString());
				migratedKeys++;
			}
		}
		if (log) {
			log(Level.INFO, "Legacy variable migration is complete. " + migratedKeys + " keys were migrated.");
		}
		return migratedKeys != 0;
	}

	public void saveResource(String resourcePath, String resultPath, boolean replace) {
		if (resourcePath == null || resourcePath.isEmpty())
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		if (resultPath == null || resultPath.isEmpty())
			throw new IllegalArgumentException("ResultPath cannot be null or empty");

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = (extension != null)
				? extension.getResource(resourcePath)
				: Main.plugin.getResource(resourcePath);

		File dataFolder = (extension != null) ? extension.getDataFolder() : Main.plugin.getDataFolder();

		if (in == null)
			throw new IllegalArgumentException(
					"The embedded resource '" + resourcePath + "' cannot be found in " + dataFolder);
		File outFile = new File(resultPath);
		File outDir = outFile.getParentFile();
		if (outDir != null && !outDir.exists())
			outDir.mkdirs();

		try {
			if (!outFile.exists() || replace) {
				if (!outFile.exists()) {
					outFile.createNewFile();
				}

				OutputStream out = Files.newOutputStream(outFile.toPath());
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0)
					out.write(buf, 0, len);
				out.close();
				in.close();
			} else {
				log(Level.WARNING, "Could not save " + resourcePath + " to " + outFile
						+ " because " + outFile.getName() + " already exists.");
			}
		} catch (IOException ex) {
			log(Level.SEVERE, "Could not save " + resourcePath + " to " + resultPath, ex);
		}
	}

	private void log(Level level, String message) {
		if (extension != null) {
			extension.getLogger().log(level, message);
		} else {
			Main.plugin.getLogger().log(level, "[BetterTeams] " + message);
		}
	}
	private void log(Level level, String message, Throwable ex) {
		if (extension != null) {
			extension.getLogger().log(level, message, ex);
		} else {
			Main.plugin.getLogger().log(level, "[BetterTeams] " + message, ex);
		}
	}

}
