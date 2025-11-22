package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.exceptions.LoadingException;
import com.booksaw.betterTeams.util.ExtUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ExtensionManager {
	private final Main plugin;
	private final File extensionsDir;
	@Getter
	private final ExtensionLoader loader;
	@Getter
	private final ExtensionStore store;

	private final Set<String> failedExtensions = new HashSet<>();

	public ExtensionManager(Main plugin, File extensionsDir) {
		this(plugin, extensionsDir, new ExtensionLoader(plugin), new ExtensionStore());
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
		failedExtensions.clear();

		for (ExtensionInfo info : sorted) {
			String name = info.getName();

			if (hasFailedDependency(info)) {
				plugin.getLogger().warning("Skipping extension '" + name + "' because one of its dependencies failed to load.");
				failedExtensions.add(name);
				continue;
			}

			try {
				ExtensionWrapper wrapper = loader.load(info);
				this.store.add(wrapper);
			} catch (LoadingException e) {
				plugin.getLogger().log(Level.SEVERE, "Failed to load extension: " + name + " (" + e.getMessage() + ")");
				failedExtensions.add(name);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while loading " + name, e);
				failedExtensions.add(name);
			}
		}
	}

	public void enableExtensions() {
		plugin.getLogger().info("Enabling extensions...");
		List<String> loadOrder = this.store.getLoadOrder();
		for (String name : loadOrder) {
			ExtensionWrapper wrapper = this.store.get(name);
			if (wrapper == null) continue;

			try {
				loader.enable(wrapper);
			} catch (LoadingException e) {
				plugin.getLogger().log(Level.SEVERE, "Failed to enable extension: " + name, e);
				wrapper.setEnabled(false);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Unexpected error enabling " + name, e);
				wrapper.setEnabled(false);
			}
		}
	}

	public void disableExtensions() {
		plugin.getLogger().info("Disabling extensions...");
		List<String> loadOrderReversed = this.store.getLoadOrderReversed();

		for (String name : loadOrderReversed) {
			ExtensionWrapper wrapper = this.store.get(name);
			if (wrapper != null && wrapper.isEnabled()) {
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
		}
		this.store.clear();
		this.failedExtensions.clear();
	}

	public void unloadExtension(BetterTeamsExtension instance) {
		ExtensionWrapper wrapper = store.get(instance);
		if (wrapper != null) {
			loader.unload(wrapper);
			store.remove(wrapper.getInfo().getName());
		}
	}

	public boolean isEnabled(String name) {
		return store.get(name) != null && store.get(name).isEnabled();
	}

	private boolean hasFailedDependency(ExtensionInfo info) {
		for (String dep : info.getExtensionDepend()) {
			if (failedExtensions.contains(dep)) {
				return true;
			}
		}
		return false;
	}
}
