package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;


public abstract class BetterTeamsExtension {

	private ExtensionInfo info;
	private File dataFolder;
	private Main plugin;
	private ExtensionLogger extensionLogger;

	private FileConfiguration config = null;
	private File configFile = null;

	/**
	 * Called when the extension is enabled.
	 */
	public void onEnable() {}

	/**
	 * Called when the extension is disabled.
	 */
	public void onDisable() {}

	/**
	 * Called when the extension is loaded, before it is enabled.
	 */
	public void onLoad() {}


	/**
	 * just let me disable from this server
	 */
	public final void selfDisable() {
		plugin.getExtensionManager().unloadExtension(this);
	}

	/**
	 * Gets the main BetterTeams plugin instance.
	 * @return The main plugin.
	 */
	@NotNull
	public final Main getPlugin() {
		return this.plugin;
	}

	/**
	 * Gets the information about this extension, loaded from the extension.yml file.
	 * @return The extension's info.
	 */
	@NotNull
	public final ExtensionInfo getInfo() {
		return this.info;
	}

	/**
	 * Gets the logger for this extension.
	 * @return The extension's logger.
	 */
	@NotNull
	public final ExtensionLogger getLogger() {
		if (extensionLogger == null) {
			return new ExtensionLogger(plugin.getLogger(), info.getName());
		}
		return extensionLogger;
	}

	/**
	 * Gets the data folder for this extension, located at /plugins/BetterTeams/extensions/{extension-name}/
	 * @return The extension's dedicated data folder.
	 */
	@NotNull
	public final File getDataFolder() {
		return this.dataFolder;
	}

	/**
	 * Gets the extension's configuration from config.yml.
	 * @return The YamlConfiguration for this extension.
	 */
	@NotNull
	public FileConfiguration getConfig() {
		if (config == null) {
			reloadConfig();
		}
		return config;
	}

	/**
	 * Reloads the config.yml from disk.
	 */
	public void reloadConfig() {
		configFile = getConfigFile();
		config = YamlConfiguration.loadConfiguration(configFile);

		try (InputStream def = getResource("config.yml")) {
			if (def == null) return;
			try (InputStreamReader reader = new InputStreamReader(def, StandardCharsets.UTF_8)) {
				config.setDefaults(YamlConfiguration.loadConfiguration(reader));
			}
		} catch (IOException | IllegalArgumentException ignore) {}
	}

	private File getConfigFile() {
		if (configFile == null) configFile = new File(getDataFolder(), "config.yml");
		return configFile;
	}

	/**
	 * Saves the raw "config.yml" resource from the extension's JAR to its data folder if it doesn't exist.
	 */
	public void saveDefaultConfig() {
		saveResource("config.yml", false);
	}

	/**
	 * Saves the current configuration to the config.yml file.
	 */
	public void saveConfig() {
		try {
			getConfig().save(getConfigFile());
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Could not save config", e);
		}
	}

	/**
	 * Saves any embedded resource to the extension’s data folder.
	 * @param resourcePath path inside the jar (use '/' separators)
	 * @param replace      overwrite existing file
	 * @throws IllegalArgumentException if resource not found
	 */
	public void saveResource(@NotNull String resourcePath, boolean replace) {
		if (resourcePath.isEmpty()) {
			throw new IllegalArgumentException("ResourcePath cannot be empty");
		}
		resourcePath = resourcePath.replace('\\', '/');

		try (InputStream in = getResource(resourcePath)) {
			if (in == null) {
				throw new IllegalArgumentException("Resource '" + resourcePath + "' not found in " + getInfo().getName());
			}

			File outFile = new File(dataFolder, resourcePath);
			File outDir = outFile.getParentFile();
			if (outDir != null && !outDir.exists()) {
				outDir.mkdirs();
			}
			if (!outFile.exists() || replace) {
				Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save resource " + resourcePath, ex);
		}
	}

	/**
	 * Gets a raw resource from the extension jar.
	 * @param filename path inside the jar
	 * @return stream or null if not found
	 */
	@Nullable
	public InputStream getResource(@NotNull String filename) {
		if (filename.isEmpty()) {
			throw new IllegalArgumentException("ResourcePath cannot be empty");
		}

		ClassLoader cl = getClass().getClassLoader();
		if (!(cl instanceof URLClassLoader ucl)) {
			return cl.getResourceAsStream(filename);
		}

		URL[] urLs = ucl.getURLs();
		if (urLs.length == 0) {
			return null;
		}
		URL jarUrl = urLs[0];

		try {
			String resourcePath = filename.replace('\\', '/');
			if (resourcePath.startsWith("/")) {
				resourcePath = resourcePath.substring(1);
			}

			URL resourceUrl = new URL("jar:" + jarUrl.toExternalForm() + "!/" + resourcePath);
			return resourceUrl.openStream();
		} catch (IOException e) {
			return null;
		}
	}

	protected final void init(ExtensionInfo info, File dataFolder, Main plugin) {
		this.info = info;
		this.dataFolder = dataFolder;
		this.plugin = plugin;

		this.extensionLogger = new ExtensionLogger(plugin.getLogger(), info.getName());
	}
}

