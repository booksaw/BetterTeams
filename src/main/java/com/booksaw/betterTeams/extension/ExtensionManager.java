package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.util.ExtUtil;

import java.io.File;
import java.util.Set;

public class ExtensionManager {
	private final Main plugin;
	private final File extensionsDir;

	public ExtensionManager(Main plugin, File extensionsDir) {
		this.plugin = plugin;
		this.extensionsDir = extensionsDir;
	}


	public void initializeExtensions() {
		plugin.getLogger().info("Initializing extensions...");
		Set<ExtensionInfo> scanned = ExtUtil.scanExtensions(extensionsDir);

		if (scanned.isEmpty()) {
			plugin.getLogger().info("No extensions to load");
			return;
		}

		// TODO
	}
}
