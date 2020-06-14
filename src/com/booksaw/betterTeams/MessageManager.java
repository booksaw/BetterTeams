package com.booksaw.betterTeams;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

/**
 * Used to control all communications to the user
 * 
 * @author booksaw
 *
 */
public class MessageManager {

	/**
	 * This is used to store the configuration file which has all the messages
	 * within
	 */
	private static FileConfiguration messages;

	/**
	 * This is the prefix which goes before all messages related to this plugin
	 */
	private static String prefix;

	/**
	 * This method is used to provide the configuration file in which all the
	 * message references are stored, this method also loads the default prefix
	 * 
	 * @param file the configuration file
	 */
	public static void addMessages(FileConfiguration file) {
		messages = file;
		prefix = ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("prefixFormat"));
	}

	/**
	 * Used to send a message to the specified user
	 * 
	 * @param sender    the commandSender which the message should be sent to
	 * @param reference the reference for the message
	 */
	public static void sendMessage(CommandSender sender, String reference) {

		String message = ChatColor.translateAlternateColorCodes('&', messages.getString(reference));
		if (message == null) {
			Bukkit.getLogger().log(Level.WARNING, "Message with the reference " + reference + " does not exist");
			return;
		}

		sender.sendMessage(prefix + message);

	}

	/**
	 * Used to send a formatted message
	 * 
	 * @param sender      the commandSender which the message should be sent to
	 * @param reference   the reference for the message
	 * @param replacement the value that the placeholder should be replaced with
	 */
	public static void sendMessageF(CommandSender sender, String reference, String replacement) {

		String message = ChatColor.translateAlternateColorCodes('&', messages.getString(reference));
		if (message == null) {
			Bukkit.getLogger().log(Level.WARNING, "Message with the reference " + reference + " does not exist");
			return;
		}

		message = String.format(prefix + message, replacement);

		sender.sendMessage(message);

	}

	/**
	 * This is used to get the message from the provided location in the
	 * Configuration file, this does not add a prefix to the message
	 * 
	 * @param reference the reference for the message
	 * @return the message (without prefix)
	 */
	public static String getMessage(String reference) {
		return ChatColor.translateAlternateColorCodes('&', messages.getString(reference));
	}

	public static FileConfiguration getMessages() {
		return messages;
	}

	/**
	 * Used to get the prefix for all messages
	 * 
	 * @return
	 */
	public static String getPrefix() {
		return prefix;
	}

}
