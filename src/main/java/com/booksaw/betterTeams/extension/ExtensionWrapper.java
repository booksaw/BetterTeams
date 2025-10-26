package com.booksaw.betterTeams.extension;

import lombok.Data;

import java.io.File;
import java.net.URLClassLoader;

/**
 * A wrapper class that holds a loaded {@link BetterTeamsExtension}.
 */
@Data
public class ExtensionWrapper {

	/**
	 * Holds metadata about the extension.
	 */
	private final ExtensionInfo info;

	/**
	 * The actual instance of the extension's main class.
	 */
	private final BetterTeamsExtension instance;

	/**
	 * The classloader used to load this extension, isolating its dependencies.
	 */
	private final URLClassLoader classLoader;

	/**
	 * The dedicated data folder for this extension.
	 */
	private final File dataFolder;

	/**
	 * The current lifecycle state of the extension.
	 */
	private ExtensionState state;
}


