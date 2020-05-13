package com.booksaw.betterTeams;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Used to control all communications to the user
 * 
 * @author booksaw
 *
 */
public class MessageManager {

	private static HashMap<String, String> messages = new HashMap<>();

	/**
	 * Used to load a new message into the message manager
	 * 
	 * @param reference
	 * @param message
	 */
	public static void addMessage(String reference, String message) {
		messages.put(reference, message);
	}

	/**
	 * Used to send a message to the specified user
	 * 
	 * @param sender    the commandSender which the message should be sent to
	 * @param reference the reference for the message
	 */
	public static void sendMessasge(CommandSender sender, String reference) {

		String message = messages.get("reference");
		if (message == null) {
			Bukkit.getLogger().log(Level.WARNING, "Message with the reference " + reference + " does not exist");
			return;
		}

		sender.sendMessage(message);

	}

	/**
	 * Used to send a formatted message
	 * 
	 * @param sender      the commandSender which the message should be sent to
	 * @param reference   the reference for the message
	 * @param replacement the value that the placeholder should be replaced with
	 */
	public static void sendMessasgeF(CommandSender sender, String reference, String replacement) {

		String message = messages.get("reference");
		if (message == null) {
			Bukkit.getLogger().log(Level.WARNING, "Message with the reference " + reference + " does not exist");
			return;
		}

		message = String.format(message, replacement);

		sender.sendMessage(message);

	}

}
