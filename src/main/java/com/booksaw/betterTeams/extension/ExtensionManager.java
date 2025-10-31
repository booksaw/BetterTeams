package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.exceptions.LoadingException;
import com.booksaw.betterTeams.util.ExtUtil;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class ExtensionManager {
	private final Main plugin;
	private final File extensionsDir;
	@Getter
	private final ExtensionLoader loader;
	@Getter
	private final ExtensionStore store;

	public ExtensionManager(Main plugin, File extensionsDir) {
		this.plugin = plugin;
		this.extensionsDir = extensionsDir;
		this.store = new ExtensionStore();
		this.loader = new ExtensionLoader(plugin);
	}


	public void initializeExtensions() {
		plugin.getLogger().info("Initializing extensions...");
		Set<ExtensionInfo> scanned = ExtUtil.scanExtensions(extensionsDir);

		if (scanned.isEmpty()) {
			plugin.getLogger().info("No extensions to load");
			return;
		}

		Set<ExtensionInfo> sorted = ExtUtil.sort(scanned);
		if (sorted == null) {
			plugin.getLogger().severe("Circular dependency detected! Could not load extensions.");
			return;
		}

		plugin.getLogger().info("Loading extensions...");
		for (ExtensionInfo info : sorted) {
			try {
				ExtensionWrapper wrapper = loader.load(info);
				this.store.add(wrapper);
			} catch (LoadingException e) {
				plugin.getLogger().log(Level.SEVERE, "Failed to load extension: " + info.getName(), e.getCause() != null ? e.getCause() : e);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while loading " + info.getName(), e);
			}
		}
	}

	public void enableExtensions() {
		plugin.getLogger().info("Enabling extensions...");
		List<String> loadOrder = this.store.getLoadOrder();
		for (String name : loadOrder) {
			ExtensionWrapper wrapper = this.store.get(name);
			if (wrapper == null) {
				continue;
			}

			try {
				loader.enable(wrapper);
			} catch (LoadingException e) {
				plugin.getLogger().log(Level.SEVERE, "Failed to enable extension: " + name, e.getCause() != null ? e.getCause() : e);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while enabling " + name, e);
			}
		}
	}

	public void disableExtensions() {
		plugin.getLogger().info("Disabling extensions...");
		List<String> loadOrderReversed = this.store.getLoadOrderReversed();

		for (String name : loadOrderReversed) {
			ExtensionWrapper wrapper = this.store.get(name);
			if (wrapper != null && wrapper.getEnabled()) {
				loader.disable(wrapper);
			}
		}
	}

	public void unloadExtensions() {
		plugin.getLogger().info("Unloading extensions...");
		List<String> loadOrderReversed = this.store.getLoadOrderReversed();

		for (String name : loadOrderReversed) {
			ExtensionWrapper wrapper = this.store.get(name);
			if (wrapper != null) {
				loader.unload(wrapper);
			}
			this.store.clear();
		}
	}

	public void disableExtension(BetterTeamsExtension instance) {
		ExtensionWrapper wrapper = store.get(instance);
		if (wrapper != null) {
			if (wrapper.getEnabled()) {
				loader.disable(wrapper);
			}
		}
	}


}
