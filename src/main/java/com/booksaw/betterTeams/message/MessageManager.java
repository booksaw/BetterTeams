package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.ConfigManager;
import com.booksaw.betterTeams.Main;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to control all communications to the user
 *
 * @author booksaw
 */
public class MessageManager {

	public static final String MISSINGMESSAGES_FILENAME = "missingmessages.txt";

	/**
	 * Once initialized on enable, this cannot be closed until the server is shut
	 * down.
	 * <p>
	 * Serves for sending {@code Component} messages to players and console.
	 * <p>
	 * If it's not initialized after enabling the plugin, messages will be sent as
	 * legacy strings.
	 */
	private static BukkitAudiences audiences;

	/**
	 * Used to store all loaded messages
	 */
	@Getter
	private static HashMap<String, String> messages = new HashMap<>();

	@Getter
	private static ConfigManager defaultMessagesConfigManager;

	/**
	 * This is the prefix which goes before all messages related to this plugin
	 */
	@Getter
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

	private static String properlyTranslate(String message) {
		if (audiences != null) {
			return Formatter.absoluteTranslate(message);
		} else {
			return Formatter.legacyTranslate(message);
		}
	}

	private static String completeMessage(String message, boolean prefixMessage, boolean doChatFormat) {
		if (doChatFormat) {
			message = properlyTranslate(message);
		}

		if (prefixMessage) {
			message = prefix + message;
		}

		return message;
	}

	public static @Internal void initAdventure() {
		if (audiences != null) {
			return;
		}
		try {
			audiences = BukkitAudiences.create(Main.plugin);
		} catch (Exception e) {
			Main.plugin.getLogger().log(Level.WARNING,
					"Failed to initialize Adventure. MiniMessage support will not be provided", e);
		}
	}

	public static @Internal void closeAdventure() {
		if (audiences != null) {
			audiences.close();
			audiences = null;
		}
	}

	public static boolean isAdventure() {
		return audiences != null;
	}

	public static BukkitAudiences getBukkitAudiences() {
		return audiences;
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
	 * @param configManager the configuration manager
	 */
	public static void addMessages(@NotNull ConfigManager configManager) {
		prefix = Objects.requireNonNull(Main.plugin.getConfig().getString("prefixFormat"));
		if (audiences != null) {
			prefix = Formatter.absoluteTranslate(prefix);
		} else {
			prefix = Formatter.legacyTranslate(prefix);
		}
		defaultMessagesConfigManager = configManager;

		addMessages(configManager.config, false);
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

	private static void addMessages(@NotNull FileConfiguration file, boolean backup) {

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

			Logger logger = Main.plugin.getLogger();
			logger.info("==================================================================");
			logger.info(
					"Messages are missing from your selected language, the following messages are using english:");

			for (String str : backupMessages) {
				logger.info("- " + str + ": " + messages.get(str));
			}

			logger.info(
					"If you are able to help with translation please join the discord server and make yourself known (https://discord.gg/JF9DNs3)");
			logger.info(
					"A file called `" + MISSINGMESSAGES_FILENAME
							+ "` has been created within this plugins folder. To contribute to the community translations, translate the messages within it and submit it to the discord");
			logger.info("==================================================================");
		}

	}

	private static void saveMissingMessages(List<String> missingMessages) {

		File f = new File(Main.plugin.getDataFolder() + File.separator + MISSINGMESSAGES_FILENAME);

		List<String> existingKeys = new ArrayList<>();

		if (f.exists()) {

			try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					String[] split = line.split(": ", 2);
					if (split.length == 2) {
						existingKeys.add(split[0]);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

		} else {
			try {
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		try (PrintWriter writer = new PrintWriter(new FileWriter(f, !existingKeys.isEmpty()))) {
			if (existingKeys.isEmpty()) {
				writer.println(
						"# Please translate these messages and then submit them to the Booksaw Development (https://discord.gg/JF9DNs3) in the #messages-submissions channel for a special rank");
				writer.println("# Your translations will be included in the next update");
				writer.println(
						"# When you are done translating, run '/teama importmessages' to include the translated messages in your main file");
			}
			for (String str : missingMessages) {
				if (!existingKeys.contains(str)) {
					writer.println(str + ": " + messages.get(str));
				}
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

	public static void sendMessage(@Nullable CommandSender sender, String reference, Object... replacement) {
		if (sender == null) {
			return;
		}

		String message = getMessage(sender, true, reference, replacement);
		if (message.isEmpty()) {
			return;
		}

		sendFullMessage(sender, message, true, false);
	}

	public static String getMessage(String reference, Object... replacement) {
		return getMessage(false, reference, replacement);
	}

	/**
	 * This is used to get the message from the provided location in the
	 * Configuration file, this does not add a prefix to the message
	 *
	 * @param reference the reference for the message
	 * @return the message (without prefix)
	 */
	public static String getMessage(boolean doChatFormat, String reference, Object... replacement) {
		try {
			if (!messages.containsKey(reference)) {
				Main.plugin.getLogger().warning("Could not find the message with the reference " + reference);
				return "";
			}

			String msg = messages.get(reference);
			if (msg.isEmpty()) {
				return "";
			}

			msg = format(msg, replacement);

			if (!doChatFormat) {
				return msg;
			} else {
				return properlyTranslate(msg);
			}
		} catch (NullPointerException e) {
			Main.plugin.getLogger().warning("Could not find the message with the reference " + reference);
			return "";
		}
	}

	public static String getMessage(@Nullable CommandSender sender, String reference, Object... replacement) {
		return getMessage(sender, false, reference, replacement);
	}

	public static String getMessage(
			@Nullable CommandSender sender,
			boolean doChatFormat,
			String reference,
			Object... replacement) {
		try {
			String msg = getMessage(reference, replacement);
			if (msg.isEmpty()) {
				return "";
			}

			if (sender instanceof Player && Main.placeholderAPI) {
				msg = PlaceholderAPI.setPlaceholders((Player) sender, msg);
			}

			if (!doChatFormat) {
				return msg;
			} else {
				return properlyTranslate(msg);
			}
		} catch (NullPointerException e) {
			Main.plugin.getLogger().warning("Could not find the message with the reference " + reference);
			return "";
		}
	}

	public static String format(String content, Object... replacement) {
		if (content == null || content.isEmpty())
			return "";
		if (replacement == null || replacement.length == 0)
			return content;

		String formatted = content;
		for (int i = 0; i < replacement.length; i++) {
			formatted = formatted.replace("{" + i + "}", replacement[i].toString());
		}
		return formatted;
	}

	/**
	 * Used when you are sending a user a message instead of a message loaded from a
	 * file
	 *
	 * @param sender  the player who sent the command
	 * @param message the message to send to that user
	 */
	public static void sendFullMessage(@Nullable CommandSender sender, String message) {
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
	public static void sendFullMessage(@Nullable CommandSender sender, String message, boolean prefixMessage) {
		sendFullMessage(sender, message, prefixMessage, true);
	}

	private static void sendFullMessage(
			@Nullable CommandSender sender,
			String message,
			boolean prefixMessage,
			boolean doChatFormat) {
		if (sender == null) {
			return;
		}

		message = completeMessage(message, prefixMessage, doChatFormat);

		if (audiences != null) {
			sendFullMessage(sender, Formatter.deserializeWithMiniMessage(message));
		} else {
			sender.sendMessage(message);
		}
	}

	/**
	 * Sends a component message to the specified command sender.
	 * <p>
	 * This method asumes that adventure has been initialized, so it should be
	 * (otherwise, it'll fail and errors may appear).
	 *
	 * @param sender  the player who sent the command
	 * @param message the message to send to that user
	 */
	public static void sendFullMessage(@Nullable CommandSender sender, Component message) {
		if (sender == null) {
			return;
		}
		if (sender instanceof Player) {
			audiences.player((Player) sender).sendMessage(message);
		} else if (sender instanceof ConsoleCommandSender) {
			audiences.console().sendMessage(message);
		} else {
			audiences.sender(sender).sendMessage(message);
		}
	}

	public static void sendMessage(
			@Nullable Collection<? extends CommandSender> senders,
			@Nullable Player player,
			String reference,
			Object... replacement) {
		sendMessage(senders, player, true, reference, replacement);
	}

	public static void sendMessage(
			@Nullable Collection<? extends CommandSender> senders,
			@Nullable Player player,
			boolean prefixFormat,
			String reference,
			Object... replacement) {
		sendMessage(senders, player, prefixFormat, true, reference, replacement);
	}

	/**
	 * Used when sending a referenced message (formatted around a single player)
	 * to a group of command senders.
	 * 
	 * @param senders
	 * @param sender
	 * @param message
	 * @param prefixFormat
	 * @param doChatFormat
	 */
	public static void sendMessage(
			@Nullable Collection<? extends CommandSender> senders,
			@Nullable Player player,
			boolean prefixFormat,
			boolean doChatFormat,
			String reference,
			Object... replacement) {
		if (senders == null || senders.isEmpty()) {
			return;
		}

		if (player == null) {
			return;
		}

		String message = getMessage(player, reference, replacement);

		if (message == null || message.isEmpty()) {
			return;
		}

		sendBulkMessage(senders, completeMessage(message, prefixFormat, doChatFormat));
	}

	/**
	 * Used when sending a raw message
	 * to a group of command senders.
	 * 
	 * @param senders
	 * @param message
	 * @param prefixFormat
	 * @param doChatFormat
	 */
	public static void sendFullMessage(
			@Nullable Collection<? extends CommandSender> senders,
			String message,
			boolean prefixFormat,
			boolean doChatFormat) {
		if (senders == null || senders.isEmpty()) {
			return;
		}
		sendBulkMessage(senders, completeMessage(message, prefixFormat, doChatFormat));
	}

	public static void sendBulkMessage(@NotNull Collection<? extends CommandSender> senders, String message) {
		if (audiences != null) {
			Component messageComponent = Formatter.deserializeWithMiniMessage(message);
			for (CommandSender sender : senders) {
				sendFullMessage(sender, messageComponent);
			}
		} else {
			for (CommandSender sender : senders) {
				sender.sendMessage(message);
			}
		}
	}

	@Contract(value = " -> new", pure = true)
	public static @NotNull File getFile() {
		return new File("plugins/BetterTeams/" + lang + ".yml");
	}

	public static FileConfiguration getDefaultMessages() {
		return defaultMessagesConfigManager.config;
	}

	/**
	 * Used to clear all messages from the cache
	 */
	public static void dumpMessages() {
		messages = new HashMap<>();
		defaultMessagesConfigManager = null;
	}

	public static void sendTitle(
			@Nullable Player player,
			String reference,
			Object... replacement) {
		sendTitle(player, false, reference, replacement);
	}

	public static void sendTitle(
			@Nullable Player player,
			boolean prefixFormat,
			String reference,
			Object... replacement) {
		sendTitle(player, prefixFormat, true, reference, replacement);
	}

	/**
	 * Used to send a (formatted) title to the specified user
	 *
	 * @param player      the commandSender which the message should be sent to
	 * @param reference   the reference for the message
	 * @param replacement the value that the placeholder should be replaced with
	 */
	public static void sendTitle(
			@Nullable Player player,
			boolean prefixFormat,
			boolean doChatFormat,
			String reference,
			Object... replacement) {
		String message = getMessage(player, reference, replacement);
		sendFullTitle(player, message, prefixFormat, doChatFormat);
	}

	public static void sendFullTitle(@Nullable Player player, String message) {
		sendFullTitle(player, message, true);
	}

	public static void sendFullTitle(
			@Nullable Player player,
			String message,
			boolean prefixMessage) {
		sendFullTitle(player, message, prefixMessage, true);
	}

	public static void sendFullTitle(
			@Nullable Player player,
			String message,
			boolean prefixMessage,
			boolean doChatFormat) {
		if (player == null) {
			return;
		}

		if (message.isEmpty()) {
			return;
		}

		message = completeMessage(message, prefixMessage, doChatFormat);

		// fadeIn - time in ticks for titles to fade in. Defaults to 10.
		// stay - time in ticks for titles to stay. Defaults to 70.
		// fadeOut - time in ticks for titles to fade out. Defaults to 20.
		if (audiences != null) {
			audiences.player(player).showTitle(Title.title(Formatter.deserializeWithMiniMessage(message),
					Component.empty(), Title.Times.times(Ticks.duration(10), Ticks.duration(100), Ticks.duration(20))));
		} else {
			player.sendTitle(message, "", 10, 100, 20);
		}
	}
}
