package com.booksaw.betterTeams.extension;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.URLClassLoader;

/**
 * A wrapper class that holds a loaded {@link BetterTeamsExtension}.
 */
@RequiredArgsConstructor
@Getter
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
	 * The current lifecycle state of the extension.
	 */
	@Setter
	private boolean enabled = false;
}


