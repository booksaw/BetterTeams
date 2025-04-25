package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.ConfigManager;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Utils;

import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
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
import java.util.logging.Logger;

/**
 * Used to control all communications to the user
 *
 * @author booksaw
 */
public class MessageManager {

	public static final String MISSINGMESSAGES_FILENAME = "missingmessages.txt";

	private static MessageSender messageSender;

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

	public static @Internal void setupMessageSender() {
		if (messageSender != null)
			return;

		try {
			BukkitAudiences audiences = BukkitAudiences.create(Main.plugin);
			messageSender = new AdventureMessageSender(audiences);
		} catch (Exception e) {
			Main.plugin.getLogger().warning(
					"Failed to initialize AdventureMessageSender. Using LegacyMessageSender instead.");
			messageSender = new LegacyMessageSender();
		}
	}

	public static @Internal void dumpMessageSender() {
		if (messageSender == null)
			return;

		AdventureMessageSender adventure;
		if ((adventure = advntMessageSender()) != null) {
			adventure.closeAdventure();
		}

		messageSender = null;
	}

	public static boolean isMessageSenderInit() {
		return messageSender != null;
	}

	public static boolean isAdventure() {
		return messageSender instanceof AdventureMessageSender;
	}

	private static @Nullable AdventureMessageSender advntMessageSender() {
		return isAdventure() ? (AdventureMessageSender) messageSender : null;
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
		// prefix =
		// Objects.requireNonNull(Main.plugin.getConfig().getString("prefixFormat"));

		// Should prefix default to empty string ?
		prefix = Main.plugin.getConfig().getString("prefixFormat", "");

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

	public static @NotNull String getMessage(String reference) {
		try {
			if (!messages.containsKey(reference)) {
				Main.plugin.getLogger().warning("Could not find the message with the reference " + reference);
				return "";
			}

			String msg = messages.get(reference);
			if (msg.isEmpty()) return "";

			return msg;

		} catch (NullPointerException e) {
			Main.plugin.getLogger().warning("Could not find the message with the reference " + reference);
			return "";
		}
	}

	/**
	 * This is used to get the message from the provided location in the
	 * Configuration file, this does not add a prefix to the message
	 *
	 * @param reference    the reference for the message
	 * @param replacements the replacements for the {n} placeholders ( {0}, {1},
	 *                     {2}, ... )
	 * @return the message (without prefix)
	 */
	public static @NotNull String getMessage(String reference, Object... replacements) {
		return Formatter.setPlaceholders(getMessage(reference), replacements);
	}

	public static @NotNull String getMessage(OfflinePlayer player, String reference, Object... replacements) {
		try {
			String msg = getMessage(reference);
			if (msg.isEmpty()) return "";

			// PAPI first, then format
			return Formatter.setPlaceholders(Formatter.setPlaceholders(msg, player), replacements);

		} catch (NullPointerException e) {
			Main.plugin.getLogger().warning("Could not find the message with the reference " + reference);
			return "";
		}
	}

	/**
	 * This method replaces placeholders in the given {@code content}
	 * string
	 * with the provided {@code replacement} values. Each placeholder
	 * must be
	 * written as {n} where {@code n} is the index of the replacement.
	 * <p>
	 * 
	 * @deprecated Use {@link Formatter#setPlaceholders(String, Object...)} instead.
	 *             This method is deprecated in favor of a more explicitly named
	 *             alternative
	 *             that clarifies its behavior of replacing indexed placeholders
	 *             like {0}, {1}, etc.
	 *
	 * @param content     the string containing indexed placeholders
	 * @param replacement the values to substitute into the placeholders
	 * @return the formatted string with placeholders replaced
	 */
	@Deprecated
	public static @NotNull String format(String content, Object... replacements) {
		return Formatter.setPlaceholders(content, replacements);
	}

	public static void sendMessage(@Nullable CommandSender recipient, String reference, Object... replacements) {
		sendMessage(recipient, true, reference, replacements);
	}

	/**
	 * Used to send a (formatted) message to the specified user
	 *
	 * @param recipient    the commandSender which the message should be sent to
	 * @param doPrefix     if the message should have prefix
	 * @param reference    the reference for the message
	 * @param replacements the value that the placeholder should be replaced with
	 */

	public static void sendMessage(@Nullable CommandSender recipient, boolean doPrefix, String reference, Object... replacements) {
		if (recipient == null) return;

		String message;
		if (recipient instanceof Player) {
			message = getMessage((Player) recipient, reference, replacements);
		} else {
			message = getMessage(reference, replacements);
		}

		if (message.isEmpty()) return;

		messageSender.sendFullMessage(recipient, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	public static void sendMessage(
			@Nullable CommandSender recipient, @Nullable OfflinePlayer player,
			String reference, Object... replacements) {
		sendMessage(recipient, player, true, reference, replacements);
	}

	public static void sendMessage(
			@Nullable CommandSender recipient, @Nullable OfflinePlayer player,
			boolean doPrefix, String reference, Object... replacements) {
		if (recipient == null) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		messageSender.sendFullMessage(recipient, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	public static void sendMessage(
			@Nullable Collection<? extends CommandSender> senders,
			String reference, Object... replacements) {
		sendMessage(senders, null, reference, replacements);
	}

	public static void sendMessage(
			@Nullable Collection<? extends CommandSender> senders,
			boolean doPrefix, String reference, Object... replacements) {
		sendMessage(senders, null, doPrefix, reference, replacements);
	}

	public static void sendMessage(
			@Nullable Collection<? extends CommandSender> senders, @Nullable OfflinePlayer player,
			String reference, Object... replacements) {
		sendMessage(senders, player, true, reference, replacements);
	}

	public static void sendMessage(
			@Nullable Collection<? extends CommandSender> recipients, @Nullable OfflinePlayer player,
			boolean doPrefix, String reference, Object... replacements) {
		Collection<? extends CommandSender> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		messageSender.sendFullMessage(filteredRecipients, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	/**
	 * Used when you are sending a user a message instead of a message loaded from a
	 * file
	 *
	 * @param sender  the player who sent the command
	 * @param message the message to send to that user
	 */
	public static void sendFullMessage(@Nullable CommandSender sender, String message) {
		sendFullMessage(sender, message, false);
	}

	/**
	 * Used when you are sending a user a message instead of a message loaded from a
	 * file
	 *
	 * @param sender        the player who sent the command
	 * @param message       The message to send to that user
	 * @param prefixMessage The prefix for that message
	 */
	public static void sendFullMessage(@Nullable CommandSender recipient, String message, boolean doPrefix) {
		if (recipient == null) return;

		if (message == null || message.isEmpty()) return;

		messageSender.sendFullMessage(recipient, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	public static void sendFullMessage(@Nullable Collection<? extends CommandSender> senders, String message) {
		sendFullMessage(senders, message, false);
	}

	/**
	 * Used when sending a raw message
	 * to a group of command senders.
	 * 
	 * @param recipients
	 * @param message
	 * @param doPrefix
	 */
	public static void sendFullMessage(
			@Nullable Collection<? extends CommandSender> recipients,
			String message, boolean doPrefix) {
		Collection<? extends CommandSender> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		if (message == null || message.isEmpty()) return;

		messageSender.sendFullMessage(filteredRecipients, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	/**
	 * Attempts to send a full {@link Component} message to the given
	 * {@link CommandSender}
	 * using Adventure, if it is available.
	 *
	 * @param recipient the target to send the message to; may be {@code null}
	 * @param component the Adventure {@link Component} to send; may be {@code null}
	 *                  or empty
	 * @return {@code true} if adventure is available, regardless of whether
	 *         component was actually sent
	 *         (if component is {@code null}, or equals to
	 *         {@link Component#empty()}, or recipient is {@code null})
	 *         <p>
	 *         {@code false} if Adventure is unavailable
	 */
	public static boolean sendFullMessage(@Nullable CommandSender recipient, Component component) {
		AdventureMessageSender adventure;
		if ((adventure = advntMessageSender()) != null) {
			if (recipient == null) return true;

			if (component == null || component.equals(Component.empty())) return true;

			adventure.sendFullMessage(recipient, component);
			return true;
		}
		return false;
	}

	public static boolean sendFullMessage(@Nullable Collection<? extends CommandSender> recipients, Component component) {
		AdventureMessageSender adventure;
		if ((adventure = advntMessageSender()) != null) {
			Collection<? extends CommandSender> filteredRecipients = Utils.filterNonNull(recipients);
			if (filteredRecipients.isEmpty()) return true;

			if (component == null || component.equals(Component.empty())) return true;

			adventure.sendFullMessage(filteredRecipients, component);
			return true;
		}
		return false;
	}

	public static void sendTitle(
			@Nullable Player recipient,
			String reference, Object... replacements) {
		sendTitle(recipient, false, reference, replacements);
	}

	/**
	 * Used to send a (formatted) title to the specified user
	 *
	 * @param recipient    the player which the message should be sent to
	 * @param doPrefix     if the message should include the prefix or not
	 * @param reference    the reference for the message
	 * @param replacement  the value that the placeholder should be replaced with
	 */
	public static void sendTitle(
			@Nullable Player recipient,
			boolean doPrefix,
			String reference, Object... replacements) {
		if (recipient == null) return;

		String message = getMessage(recipient, reference, replacements);
		if (message.isEmpty()) return;

		messageSender.sendFullTitle(recipient, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	public static void sendTitle(
			@Nullable Player recipient, @Nullable OfflinePlayer player,
			String reference, Object... replacement) {
		sendTitle(recipient, player, false, reference, replacement);
	}

	/**
	 * Used to send a (formatted) title to the specified user
	 *
	 * @param recipient   the player which the message should be sent to
	 * @param player      the player to format this message around
	 * @param doPrefix    if the message should include the prefix or not
	 * @param reference   the reference for the message
	 * @param replacement the value that the placeholder should be replaced with
	 */
	public static void sendTitle(
			@Nullable Player recipient, @Nullable OfflinePlayer player,
			boolean doPrefix,
			String reference, Object... replacements) {
		if (recipient == null) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		messageSender.sendFullTitle(recipient, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	public static void sendTitle(
			@Nullable Collection<? extends Player> recipients,
			String reference,
			Object... replacements) {
		sendTitle(recipients, null, reference, replacements);
	}

	public static void sendTitle(
			@Nullable Collection<? extends Player> recipients,
			boolean doPrefix,
			String reference,
			Object... replacements) {
		sendTitle(recipients, null, doPrefix, reference, replacements);
	}

	public static void sendTitle(
			@Nullable Collection<? extends Player> recipients,
			@Nullable Player player,
			String reference,
			Object... replacements) {
		sendTitle(recipients, player, false, reference, replacements);
	}

	public static void sendTitle(
			@Nullable Collection<? extends Player> recipients, @Nullable Player player,
			boolean doPrefix,
			String reference, Object... replacements) {
		Collection<? extends Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		messageSender.sendFullTitle(filteredRecipients, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	public static void sendFullTitle(@Nullable Player recipient, String message) {
		sendFullTitle(recipient, message, true);
	}

	public static void sendFullTitle(@Nullable Player recipient, String message, boolean doPrefix) {
		if (recipient == null) return;

		if (message == null || message.isEmpty()) return;

		messageSender.sendFullTitle(recipient, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	public static void sendFullTitle(@Nullable Collection<? extends Player> recipients, String message) {
		sendFullTitle(recipients, message, true);
	}

	public static void sendFullTitle(@Nullable Collection<? extends Player> recipients, @Nullable String message, boolean doPrefix) {
		Collection<? extends Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		if (message == null || message.isEmpty()) return;

		messageSender.sendFullTitle(filteredRecipients, (doPrefix ? (prefix != null ? prefix : "") : "") + message);
	}

	public static boolean sendFullTitle(@Nullable Player recipient, Component component) {
		AdventureMessageSender adventure;
		if ((adventure = advntMessageSender()) != null) {
			if (recipient == null) return true;

			if (component == null || component.equals(Component.empty())) return true;

			adventure.sendFullTitle(recipient, component);
			return true;
		}
		return false;
	}

	public static boolean sendFullTitle(@Nullable Collection<? extends Player> recipients, Component component) {
		AdventureMessageSender adventure;
		if ((adventure = advntMessageSender()) != null) {
			Collection<? extends Player> filteredRecipients = Utils.filterNonNull(recipients);
			if (filteredRecipients.isEmpty()) return true;

			if (component == null || component.equals(Component.empty())) return true;

			adventure.sendFullTitle(filteredRecipients, component);
			return true;
		}
		return false;
	}

}
