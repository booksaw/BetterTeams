package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.exceptions.LoadingException;
import com.booksaw.betterTeams.util.ExtUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;

@RequiredArgsConstructor
public class ExtensionLoader {

	private final Main plugin;

	public ExtensionWrapper load(@NotNull ExtensionInfo info) throws LoadingException {
		String name = info.getName();
		File jarFile = info.getJarFile();

		if (jarFile == null) {
			throw new LoadingException("No JAR file associated with extension '" + name + "'");
		}

		URLClassLoader loader = null;
		try {
			loader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, plugin.getClass().getClassLoader());
			Class<?> clazz = Class.forName(info.getMainClass(), true, loader);

			if (!BetterTeamsExtension.class.isAssignableFrom(clazz)) {
				throw new ClassCastException(info.getMainClass() + " does not extend BetterTeamsExtension");
			}

			BetterTeamsExtension instance = (BetterTeamsExtension) clazz.getDeclaredConstructor().newInstance();
			File dataFolder = new File(plugin.getDataFolder(), "extensions/" + info.getName());

			instance.init(info, dataFolder, plugin);

			ExtensionWrapper loaded = new ExtensionWrapper(info, instance, loader);

			load(loaded);
			return loaded;
		} catch (Exception e) {
			if (loader != null) {
				try {
					loader.close();
				} catch (IOException ignored) {}
			}
			throw new LoadingException("Failed to load " + info.getName(), e);
		}
	}

	public void load(@NotNull ExtensionWrapper wrapper) throws LoadingException {
		String name = wrapper.getInfo().getName();
		try {
			wrapper.getInstance().onLoad();
			plugin.getLogger().info("Loaded extension: " + name);
		} catch (Exception e) {
			throw new LoadingException("Failed to load extension '" + name + "'", e);
		}
	}

	public ExtensionWrapper enable(@NotNull ExtensionWrapper wrapper) throws LoadingException {
		ExtensionInfo info = wrapper.getInfo();
		if (wrapper.getEnabled()) {
			return wrapper;
		}
		if (ExtUtil.missingPluginDep(info)) {
			throw new LoadingException("Cannot enable '" + info.getName() + "': Missing Bukkit plugin dependencies");
		}
		if (ExtUtil.missingExtensionDep(info)) {
			throw new LoadingException("Cannot enable '" + info.getName() + "': Missing BetterTeams extension dependencies");
		}

		try {
			wrapper.getInstance().onEnable();
			wrapper.setEnabled(true);
			plugin.getLogger().info("Enabled extension: " + info.getName());
			return wrapper;
		} catch (Exception e) {
			throw new LoadingException("Failed to enable extension '" + info.getName() + "'", e);
		}
	}

	public void disable(@NotNull ExtensionWrapper wrapper) {
		if (wrapper.getEnabled()) {
			String name = wrapper.getInfo().getName();
			try {
				wrapper.getInstance().onDisable();
			} catch (Exception e) {
				plugin.getLogger().log(Level.WARNING, "Error while disabling extension '" + name + "'", e);
			} finally {
				wrapper.setEnabled(false);
				plugin.getLogger().info("Disabled extension: " + name);
			}
		}
	}

	public void unload(@NotNull ExtensionWrapper wrapper) {
		String name = wrapper.getInfo().getName();
		disable(wrapper);
		if (wrapper.getClassLoader() != null) {
			try {
				wrapper.getClassLoader().close();
			} catch (IOException e) {
				plugin.getLogger().log(Level.WARNING, "Failed to close classloader for '" + name + "'", e);
			}
		}
		plugin.getLogger().info("Unloaded extension: " + name);
	}
}
