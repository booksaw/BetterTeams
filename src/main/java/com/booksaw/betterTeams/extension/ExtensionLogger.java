package com.booksaw.betterTeams.extension;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple logger wrapper that prepends a fixed, formatted prefix
 *
 * This utility ensures that all output from a specific extension is
 * clearly identifiable in the server console.
 *
 * @param logger The underlying {@link Logger} instance
 * @param prefix The formatted prefix
 */
public record ExtensionLogger(Logger logger, String prefix) {
	public ExtensionLogger(@NotNull Logger logger, @NotNull String prefix) {
		this.logger = logger;
		this.prefix = "[" + prefix + "] ";
	}

	/**
	 * Logs an INFO message.
	 * @param msg The message to log.
	 */
	public void info(@NotNull String msg) {
		logger.info(prefix + msg);
	}

	/**
	 * Logs an WARNING message.
	 * @param msg The message to log.
	 */
	public void warning(@NotNull String msg) {
		logger.warning(prefix + msg);
	}

	/**
	 * Logs an SEVERE message.
	 * @param msg The message to log.
	 */
	public void severe(@NotNull String msg) {
		logger.severe(prefix + msg);
	}

	/**
	 * Logs a message at the specified level.
	 * @param level The log level (e.g., {@link Level#INFO}).
	 * @param msg   The message to log.
	 */
	public void log(@NotNull Level level, @NotNull String msg) {
		logger.log(level, prefix + msg);
	}

	/**
	 * Logs a message at the specified level with an associated exception.
	 * @param level   The log level (e.g., {@link Level#SEVERE}).
	 * @param msg     The message to log.
	 * @param thrown  The {@link Throwable} to log.
	 */
	public void log(@NotNull Level level, @NotNull String msg, @NotNull Throwable thrown) {
		logger.log(level, prefix + msg, thrown);
	}

}
