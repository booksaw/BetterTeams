package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Used to control all communications to the user
 *
 * @author booksaw
 */
public class MessageManager {

	/**
	 * Used to store all loaded messages
	 */
	private static HashMap<String, String> messages = new HashMap<>();

	private static FileConfiguration defaultMessages;

	/**
	 * This is the prefix which goes before all messages related to this plugin
	 */
	private static String prefix;

	/**
	 * This is the language reference for the selected language
	 */
	private static String lang;

	/**
	 * Stopping this class being instantiated
	 */
	private MessageManager() {
	}

	public static String getLanguage() {
		return lang;
	}

	public static void setLanguage(String lang) {
		MessageManager.lang = lang;
	}

	/**
	 * This method is used to provide the configuration file in which all the
	 * message references are stored, this method also loads the default prefix
	 *
	 * @param file the configuration file
	 */
	public static void addMessages(FileConfiguration file) {
		prefix = ChatColor.translateAlternateColorCodes('&',
				Objects.requireNonNull(Main.plugin.getConfig().getString("prefixFormat")));
		defaultMessages = file;
		addMessages(file, false);

	}

	/**
	 * Used to select a file to contain backup messages in the event that the
	 * community translations are incomplete
	 * 
	 * @param file The file to load the backup messages from
	 */
	public static void addBackupMessages(YamlConfiguration file) {
		addMessages(file, true);
	}

	private static void addMessages(FileConfiguration file, boolean backup) {

		List<String> backupMessages = new ArrayList<>();

		for (String str : file.getKeys(true)) {
			if (!messages.containsKey(str)) {
				String toSave = file.getString(str);
				if (toSave == null || file.get(str) instanceof ConfigurationSection) {
					continue;
				}

				messages.put(str, toSave);

				if (backup) {
					backupMessages.add(str);
				}
			}
		}
		if (!backupMessages.isEmpty()) {

			saveMissingMessages(backupMessages);

			Logger logger = Bukkit.getLogger();
			logger.info("[BetterTeams] ==================================================================");
			logger.info(
					"[BetterTeams] Messages are missing from your selected language, the following messages are using english:");

			for (String str : backupMessages) {
				logger.info("[BetterTeams] - " + str + ": " + messages.get(str));
			}

			logger.info(
					"[BetterTeams] If you are able to help with translation please join the discord server and make yourself known (https://discord.gg/JF9DNs3)");
			logger.info(
					"[BetterTeams] A file called `missingMessages.txt` has been created within this plugins folder. To contribute to the community translations, translate the messages within it and submit it to the discord");
			logger.info("[BetterTeams] ==================================================================");
		}

	}

	private static void saveMissingMessages(List<String> missingMessages) {

		File f = new File(Main.plugin.getDataFolder() + File.separator + "missingMessages.txt");

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		try (PrintWriter writer = new PrintWriter(f)) {
			writer.println(
					"# Please translate these messages and then submit them to the Booksaw Development (https://discord.gg/JF9DNs3) in the #messages-submissions channel for a special rank");
			writer.println("# Your translations will be included in the next update");
			for (String str : missingMessages) {
				writer.println(str + ": " + messages.get(str));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Used to send a (formatted) message to the specified user
	 *
	 * @param sender      the commandSender which the message should be sent to
	 * @param reference   the reference for the message
	 * @param replacement the value that the placeholder should be replaced with
	 */
	public static void sendMessage(CommandSender sender, String reference, Object... replacement) {
		String message = getMessage(sender, reference, replacement);
		if (message.isEmpty()) {
			return;
		}

		sender.sendMessage(message);
	}

	/**
	 * This is used to get the message from the provided location in the
	 * Configuration file, this does not add a prefix to the message
	 *
	 * @param reference the reference for the message
	 * @return the message (without prefix)
	 */
	public static String getMessage(String reference, Object... replacement) {
		try {
			if (!messages.containsKey(reference)) {
				Bukkit.getLogger().warning("Could not find the message with the reference " + reference);
				return "";
			}

			String msg = messages.get(reference);
			if (msg.isEmpty()) {
				return "";
			}

			msg = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(msg));
			msg = format(msg, replacement);

			return msg;
		} catch (NullPointerException e) {
			Bukkit.getLogger().warning("Could not find the message with the reference " + reference);
			return "";
		}
	}

	public static String getMessage(CommandSender sender, String reference, Object... replacement) {
		try {
			String msg = getMessage(reference, replacement);
			if (msg.isEmpty()) {
				return "";
			}

			if (sender instanceof Player && Main.placeholderAPI) {
				msg = PlaceholderAPI.setPlaceholders((Player) sender, msg);
			}
			return ChatColor.translateAlternateColorCodes('&', msg);
		} catch (NullPointerException e) {
			Bukkit.getLogger().warning("Could not find the message with the reference " + reference);
			return "";
		}
	}

	public static String format(String content, Object... replacement) {
		if (content == null || content.isEmpty()) return "";
		if (replacement == null || replacement.length == 0) return content;

		String formatted = content;
		for (int i = 0; i < replacement.length; i++) {
			formatted = formatted.replace("{" + i + "}", replacement[i].toString());
		}
		return formatted;
	}

	public static HashMap<String, String> getMessages() {
		return messages;
	}

	/**
	 * @return the prefix for all messages Defaults to [BetterTeams] unless it is
	 *         changed by end user
	 */
	public static String getPrefix() {
		return prefix;
	}

	/**
	 * Used when you are sending a user a message instead of a message loaded from a
	 * file
	 *
	 * @param sender  the player who sent the command
	 * @param message the message to send to that user
	 */
	public static void sendFullMessage(CommandSender sender, String message) {
		sendFullMessage(sender, message, true);
	}

	/**
	 * Used when you are sending a user a message instead of a message loaded from a
	 * file
	 * 
	 * @param sender        the player who sent the command
	 * @param message       The message to send to that user
	 * @param prefixMessage The prefix for that message
	 */
	public static void sendFullMessage(CommandSender sender, String message, boolean prefixMessage) {
		if (prefixMessage) {
			sender.sendMessage(prefix + message);
		} else {
			sender.sendMessage(message);
		}
	}

	public static File getFile() {
		return new File("plugins/BetterTeams/" + lang + ".yml");
	}

	public static FileConfiguration getDefaultMessages() {
		return defaultMessages;
	}

	/**
	 * Used to clear all messages from the cache
	 */
	public static void dumpMessages() {
		messages = new HashMap<>();
		defaultMessages = null;
	}

	/**
	 * Used to send a (formatted) title to the specified user
	 *
	 * @param player      the commandSender which the message should be sent to
	 * @param reference   the reference for the message
	 * @param replacement the value that the placeholder should be replaced with
	 */
	public static void sendTitle(Player player, String reference, Object... replacement) {
		String message = getMessage(player, reference, replacement);
		sendFullTitle(player, message, false);
	}

	public static void sendFullTitle(Player player, String message) { sendFullTitle(player, message, true); }

	public static void sendFullTitle(Player player, String message, boolean prefixMessage) {
		if (prefixMessage) {
			message = prefix + message;
		}

		if (message.isEmpty()) {
			return;
		}

		// fadeIn - time in ticks for titles to fade in. Defaults to 10.
		// stay - time in ticks for titles to stay. Defaults to 70.
		// fadeOut - time in ticks for titles to fade out. Defaults to 20.
		player.sendTitle(message, "", 10, 100, 20);
	}
}
