package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.ConfigManager;
import com.booksaw.betterTeams.Main;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;


public abstract class BetterTeamsExtension {

	private ExtensionInfo info;
	private File dataFolder;
	private Main plugin;
	private ExtensionLogger extensionLogger;

	private ConfigManager configManager;
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
	public ConfigManager getConfig() {
		if (configManager == null) {
			reloadConfig();
		}
		return configManager;
	}

	/**
	 * Reloads the config.yml from disk.
	 */
	public void reloadConfig() {
		configManager = new ConfigManager("config", true, this);
	}

	/**
	 * Saves the current configuration to the config.yml file.
	 */
	public void saveConfig() {
		if (configManager != null) {
			configManager.save(false);
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
		String cleanPath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;

		try (InputStream in = getResource(cleanPath)) {
			if (in == null) {
				throw new IllegalArgumentException("Resource '" + cleanPath + "' not found in " + getInfo().getName());
			}

			File outFile = new File(dataFolder, cleanPath);
			File outDir = outFile.getParentFile();
			if (outDir != null && !outDir.exists()) {
				outDir.mkdirs();
			}
			if (!outFile.exists() || replace) {
				Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save resource " + cleanPath, ex);
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
		String path = filename.startsWith("/") ? filename.substring(1) : filename;
		URL url = null;

		if (getClass().getClassLoader() instanceof ExtensionClassLoader extLoader) {
			url = extLoader.getLocalResource(path);
		}
		if (url == null) return null;

		try {
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Could not load resource: " + path, e);
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

