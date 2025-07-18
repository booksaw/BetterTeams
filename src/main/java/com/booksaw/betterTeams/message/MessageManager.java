package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.ConfigManager;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Utils;
import com.booksaw.betterTeams.text.Formatter;
import com.booksaw.betterTeams.util.ComponentUtil;
import com.booksaw.betterTeams.util.StringUtil;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	private static MessageSender msgSender;

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

	@Getter
	private static Component prefixComponent;

	/**
	 * This is the language reference for the selected language
	 */
	private static String lang;

	/**
	 * Stopping this class being instantiated
	 */
	private MessageManager() {
	}

	/**
	 * Used to instantiate a new MessageSender.
	 * <p>
	 * Warning: This is not API, so it should never be used outside BetterTeams' package
	 */
	@Internal
	public static void setupMessageSender(BukkitAudiences audiences) {
		if (audiences != null) msgSender = new AdventureMessageSender(audiences);
		else msgSender = new LegacyMessageSender();

		Main.plugin.getLogger().info("MessageSender declared: " + msgSender);
	}

	/**
	 * Used to clear the current MessageSender.
	 * <p>
	 * Warning: This is not API, so it should never be used outside BetterTeams' package
	 */
	@Internal
	public static void dumpMessageSender() {
		msgSender = null;
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
		prefix = Main.plugin.getConfig().getString("prefixFormat", "");
		prefixComponent = prefix.isEmpty() ? Component.empty() : Formatter.absolute().process(prefix);

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
	 * @param replacements the replacements for the {n} placeholders ( {0}, {1}, {2}, ... )
	 * @return the message (without prefix)
	 */
	public static @NotNull String getMessage(String reference, Object... replacements) {
		return StringUtil.setPlaceholders(getMessage(reference), replacements);
	}

	public static @NotNull String getMessage(OfflinePlayer player, String reference, Object... replacements) {
		return StringUtil.setPlaceholders(player, getMessage(reference), replacements);
	}

	/**
	 * This method replaces placeholders in the given {@code content} string
	 * with the provided {@code replacement} values. Each placeholder must be
	 * written as {n} where {@code n} is the index of the replacement.
	 * <p>
	 *
	 * @param content      the string containing indexed placeholders
	 * @param replacements the values to substitute into the placeholders
	 * @return the formatted string with placeholders replaced
	 * @deprecated Use {@link StringUtil#setPlaceholders(String, Object...)} instead.
	 * This method is deprecated in favor of a more explicitly named alternative
	 * that clarifies its behavior of replacing indexed placeholders like {0}, {1}, etc.
	 */
	@Deprecated
	public static @NotNull String format(String content, Object... replacements) {
		return StringUtil.setPlaceholders(content, replacements);
	}

	public static void sendMessage(CommandSender recipient, String reference, Object... replacements) {
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

	public static void sendMessage(CommandSender recipient, boolean doPrefix, String reference, Object... replacements) {
		if (recipient == null) return;

		String message;
		if (recipient instanceof Player) {
			message = getMessage((Player) recipient, reference, replacements);
		} else {
			message = getMessage(reference, replacements);
		}

		if (message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendMessage(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendMessage(CommandSender recipient, @Nullable OfflinePlayer player, String reference, Object... replacements) {
		sendMessage(recipient, player, true, reference, replacements);
	}

	public static void sendMessage(CommandSender recipient, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		if (recipient == null) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendMessage(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendMessage(Collection<? extends CommandSender> recipients, String reference, Object... replacements) {
		sendMessage(recipients, true, reference, replacements);
	}

	public static void sendMessage(Collection<? extends CommandSender> recipients, boolean doPrefix, String reference, Object... replacements) {
		Collection<? extends CommandSender> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		String message = getMessage(reference, replacements);
		if (message.isEmpty()) return;

		filteredRecipients.forEach(recipient -> {
			String pMessage;
			if (recipient instanceof Player) {
				pMessage = StringUtil.setPlaceholders((Player) recipient, message);
				if (pMessage.isEmpty()) return;
			} else {
				pMessage = message;
			}

			Component c = Formatter.absolute().process(pMessage);
			msgSender.sendMessage(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
		});
	}

	public static void sendMessage(Collection<? extends CommandSender> recipients, @Nullable OfflinePlayer player, String reference, Object... replacements) {
		sendMessage(recipients, player, true, reference, replacements);
	}

	public static void sendMessage(Collection<? extends CommandSender> recipients, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		Collection<? extends CommandSender> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendMessage(filteredRecipients, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	/**
	 * Used when you are sending a user a message instead of a message loaded from a
	 * file
	 *
	 * @param sender  the player who sent the command
	 * @param message the message to send to that user
	 */
	public static void sendFullMessage(CommandSender sender, String message) {
		sendFullMessage(sender, message, false);
	}

	/**
	 * Used when you are sending a user a message instead of a message loaded from a
	 * file
	 *
	 * @param recipient The player who sent the command
	 * @param message   The message to send to that user
	 * @param doPrefix  If a prefix should be applied
	 */
	public static void sendFullMessage(CommandSender recipient, String message, boolean doPrefix) {
		if (recipient == null) return;

		if (message == null || message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendMessage(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendFullMessage(Collection<? extends CommandSender> senders, String message) {
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
	public static void sendFullMessage(Collection<? extends CommandSender> recipients, String message, boolean doPrefix) {
		Collection<? extends CommandSender> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		if (message == null || message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendMessage(filteredRecipients, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendFullMessage(CommandSender recipient, Component message) {
		sendFullMessage(recipient, message, false);
	}

	/**
	 * Sends a full {@link Component} message to the given {@link CommandSender}
	 *
	 * @param recipient the target to send the message to
	 * @param message   the Adventure {@link Component} to send
	 */
	public static void sendFullMessage(CommandSender recipient, Component message, boolean doPrefix) {
		if (recipient == null) return;

		if (message == null || message.equals(Component.empty())) return;

		msgSender.sendMessage(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, message) : message));
	}

	public static void sendFullMessage(Collection<? extends CommandSender> recipients, Component message) {
		sendFullMessage(recipients, message, false);
	}

	public static void sendFullMessage(Collection<? extends CommandSender> recipients, Component message, boolean doPrefix) {
		Collection<? extends CommandSender> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		if (message == null || message.equals(Component.empty())) return;

		msgSender.sendMessage(filteredRecipients, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, message) : message));
	}

	public static void sendTitle(Player recipient, String reference, Object... replacements) {
		sendTitle(recipient, false, reference, replacements);
	}

	/**
	 * Used to send a (formatted) title to the specified user
	 *
	 * @param recipient    the player which the message should be sent to
	 * @param doPrefix     if the message should include the prefix or not
	 * @param reference    the reference for the message
	 * @param replacements the value that the placeholder should be replaced with
	 */
	public static void sendTitle(Player recipient, boolean doPrefix, String reference, Object... replacements) {
		if (recipient == null) return;

		String message = getMessage(recipient, reference, replacements);
		if (message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendTitle(Player recipient, @Nullable OfflinePlayer player, String reference, Object... replacement) {
		sendTitle(recipient, player, false, reference, replacement);
	}

	/**
	 * Used to send a (formatted) title to the specified user
	 *
	 * @param recipient    the player which the message should be sent to
	 * @param player       the player to format this message around
	 * @param doPrefix     if the message should include the prefix or not
	 * @param reference    the reference for the message
	 * @param replacements the value that the placeholder should be replaced with
	 */
	public static void sendTitle(Player recipient, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		if (recipient == null) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendTitle(Collection<Player> recipients, String reference, Object... replacements) {
		sendTitle(recipients, false, reference, replacements);
	}

	public static void sendTitle(Collection<Player> recipients, boolean doPrefix, String reference, Object... replacements) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		String message = getMessage(reference, replacements);
		if (message.isEmpty()) return;

		filteredRecipients.forEach(recipient -> {
			String pMessage = StringUtil.setPlaceholders(recipient, message);
			if (pMessage.isEmpty()) return;

			Component c = Formatter.absolute().process(pMessage);
			msgSender.sendTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
		});
	}

	public static void sendTitle(Collection<Player> recipients, @Nullable OfflinePlayer player, String reference, Object... replacements) {
		sendTitle(recipients, player, false, reference, replacements);
	}

	public static void sendTitle(Collection<Player> recipients, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendTitle(filteredRecipients, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendFullTitle(Player recipient, String message) {
		sendFullTitle(recipient, message, false);
	}

	public static void sendFullTitle(Player recipient, String message, boolean doPrefix) {
		if (recipient == null) return;

		if (message == null || message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendFullTitle(Collection<Player> recipients, String message) {
		sendFullTitle(recipients, message, false);
	}

	public static void sendFullTitle(Collection<Player> recipients, String message, boolean doPrefix) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		if (message == null || message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendTitle(filteredRecipients, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendFullTitle(Player recipient, Component message) {
		sendFullTitle(recipient, message, false);
	}

	public static void sendFullTitle(Player recipient, Component message, boolean doPrefix) {
		if (recipient == null) return;

		if (message == null || message.equals(Component.empty())) return;

		msgSender.sendTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, message) : message));
	}

	public static void sendFullTitle(Collection<Player> recipients, Component message) {
		sendFullTitle(recipients, message, false);
	}

	public static void sendFullTitle(Collection<Player> recipients, Component message, boolean doPrefix) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		if (message == null || message.equals(Component.empty())) return;

		msgSender.sendTitle(filteredRecipients, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, message) : message));
	}

	public static void sendSubTitle(Player recipient, String reference, Object... replacements) {
		sendSubTitle(recipient, false, reference, replacements);
	}

	/**
	 * Used to send a (formatted) subtitle to the specified user
	 *
	 * @param recipient    the player which the message should be sent to
	 * @param doPrefix     if the message should include the prefix or not
	 * @param reference    the reference for the message
	 * @param replacements the value that the placeholder should be replaced with
	 */
	public static void sendSubTitle(Player recipient, boolean doPrefix, String reference, Object... replacements) {
		if (recipient == null) return;

		String message = getMessage(recipient, reference, replacements);
		if (message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendSubTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendSubTitle(Player recipient, @Nullable OfflinePlayer player, String reference, Object... replacements) {
		sendSubTitle(recipient, player, false, reference, replacements);
	}

	/**
	 * Used to send a (formatted) subtitle to the specified user
	 *
	 * @param recipient    the player which the message should be sent to
	 * @param player       the player to format this message around
	 * @param doPrefix     if the message should include the prefix or not
	 * @param reference    the reference for the message
	 * @param replacements the value that the placeholder should be replaced with
	 */
	public static void sendSubTitle(Player recipient, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		if (recipient == null) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendSubTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendSubTitle(Collection<Player> recipients, String reference, Object... replacements) {
		sendSubTitle(recipients, false, reference, replacements);
	}

	public static void sendSubTitle(Collection<Player> recipients, boolean doPrefix, String reference, Object... replacements) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		String message = getMessage(reference, replacements);
		if (message.isEmpty()) return;

		filteredRecipients.forEach(recipient -> {
			String pMessage = StringUtil.setPlaceholders(recipient, message);
			if (pMessage.isEmpty()) return;

			Component c = Formatter.absolute().process(pMessage);
			msgSender.sendSubTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
		});
	}

	public static void sendSubTitle(Collection<Player> recipients, @Nullable OfflinePlayer player, String reference, Object... replacements) {
		sendSubTitle(recipients, player, false, reference, replacements);
	}

	public static void sendSubTitle(Collection<Player> recipients, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		String message = getMessage(player, reference, replacements);
		if (message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendSubTitle(filteredRecipients, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendFullSubTitle(Player recipient, String message) {
		sendFullSubTitle(recipient, message, false);
	}

	public static void sendFullSubTitle(Player recipient, String message, boolean doPrefix) {
		if (recipient == null) return;

		if (message == null || message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendSubTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendFullSubTitle(Collection<Player> recipients, String message) {
		sendFullSubTitle(recipients, message, false);
	}

	public static void sendFullSubTitle(Collection<Player> recipients, String message, boolean doPrefix) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		if (message == null || message.isEmpty()) return;

		Component c = Formatter.absolute().process(message);
		msgSender.sendSubTitle(filteredRecipients, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, c) : c));
	}

	public static void sendFullSubTitle(Player recipient, Component message) {
		sendFullSubTitle(recipient, message, false);
	}

	public static void sendFullSubTitle(Player recipient, Component message, boolean doPrefix) {
		if (recipient == null) return;

		if (message == null || message.equals(Component.empty())) return;

		msgSender.sendSubTitle(recipient, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, message) : message));
	}

	public static void sendFullSubTitle(Collection<Player> recipients, Component message) {
		sendFullSubTitle(recipients, message, false);
	}

	public static void sendFullSubTitle(Collection<Player> recipients, Component message, boolean doPrefix) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		if (message == null || message.equals(Component.empty())) return;

		msgSender.sendSubTitle(filteredRecipients, (doPrefix ? ComponentUtil.combineComponents(prefixComponent, message) : message));
	}

	public static void sendFullTitleAndSub(Player recipient, String title, String subTitle) {
		sendFullTitleAndSub(recipient, title, subTitle, false, false);
	}

	public static void sendFullTitleAndSub(Player recipient, String title, String subTitle, boolean doPrefix, boolean doPrefixOnSub) {
		if (recipient == null) return;

		boolean titlePresent = title != null && !title.isEmpty();
		boolean subTitlePresent = subTitle != null && !subTitle.isEmpty();

		if (titlePresent && subTitlePresent) {
			Component titleComponent = Formatter.absolute().process(title);
			Component subTitleComponent = Formatter.absolute().process(subTitle);

			msgSender.sendTitleAndSub(
					recipient,
					doPrefix ? ComponentUtil.combineComponents(prefixComponent, titleComponent) : titleComponent,
					doPrefixOnSub ? ComponentUtil.combineComponents(prefixComponent, subTitleComponent) : subTitleComponent
			);
		} else if (titlePresent) {
			Component titleComponent = Formatter.absolute().process(subTitle);
			msgSender.sendSubTitle(
					recipient,
					doPrefixOnSub ? ComponentUtil.combineComponents(prefixComponent, titleComponent) : titleComponent
			);
		} else if (subTitlePresent) {
			Component subTitleComponent = Formatter.absolute().process(title);
			msgSender.sendTitle(
					recipient,
					doPrefix ? ComponentUtil.combineComponents(prefixComponent, subTitleComponent) : subTitleComponent
			);
		}
	}

	public static void sendFullTitleAndSub(Collection<Player> recipients, String title, String subTitle) {
		sendFullTitleAndSub(recipients, title, subTitle, false, false);
	}

	public static void sendFullTitleAndSub(Collection<Player> recipients, String title, String subTitle, boolean doPrefix, boolean doPrefixOnSub) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		boolean titlePresent = title != null && !title.isEmpty();
		boolean subTitlePresent = subTitle != null && !subTitle.isEmpty();

		if (titlePresent && subTitlePresent) {
			Component titleComponent = Formatter.absolute().process(title);
			Component subTitleComponent = Formatter.absolute().process(subTitle);

			msgSender.sendTitleAndSub(
					filteredRecipients,
					doPrefix ? ComponentUtil.combineComponents(prefixComponent, titleComponent) : titleComponent,
					doPrefixOnSub ? ComponentUtil.combineComponents(prefixComponent, subTitleComponent) : subTitleComponent
			);
		} else if (titlePresent) {
			Component titleComponent = Formatter.absolute().process(subTitle);
			msgSender.sendSubTitle(
					filteredRecipients,
					doPrefixOnSub ? ComponentUtil.combineComponents(prefixComponent, titleComponent) : titleComponent
			);
		} else if (subTitlePresent) {
			Component subTitleComponent = Formatter.absolute().process(title);
			msgSender.sendTitle(
					filteredRecipients,
					doPrefix ? ComponentUtil.combineComponents(prefixComponent, subTitleComponent) : subTitleComponent
			);
		}
	}

	public static void sendFullTitleAndSub(Player recipient, Component title, Component subTitle) {
		sendFullTitleAndSub(recipient, title, subTitle, false, false);
	}

	public static void sendFullTitleAndSub(Player recipient, Component title, Component subTitle, boolean doPrefix, boolean doPrefixOnSub) {
		if (recipient == null) return;

		boolean titlePresent = title != null && !title.equals(Component.empty());
		boolean subTitlePresent = subTitle != null && !subTitle.equals(Component.empty());

		if (titlePresent && subTitlePresent) {
			msgSender.sendTitleAndSub(
					recipient,
					doPrefix ? ComponentUtil.combineComponents(prefixComponent, title) : title,
					doPrefixOnSub ? ComponentUtil.combineComponents(prefixComponent, subTitle) : subTitle
			);
		} else if (titlePresent) {
			msgSender.sendTitle(
					recipient,
					doPrefix ? ComponentUtil.combineComponents(prefixComponent, title) : title
			);
		} else if (subTitlePresent) {
			msgSender.sendSubTitle(
					recipient,
					doPrefixOnSub ? ComponentUtil.combineComponents(prefixComponent, subTitle) : subTitle
			);
		}
	}

	public static void sendFullTitleAndSub(Collection<Player> recipients, Component title, Component subTitle) {
		sendFullTitleAndSub(recipients, title, subTitle, false, false);
	}

	public static void sendFullTitleAndSub(Collection<Player> recipients, Component title, Component subTitle, boolean doPrefix, boolean doPrefixOnSub) {
		Collection<Player> filteredRecipients = Utils.filterNonNull(recipients);
		if (filteredRecipients.isEmpty()) return;

		boolean titlePresent = title != null && !title.equals(Component.empty());
		boolean subTitlePresent = subTitle != null && !subTitle.equals(Component.empty());

		if (titlePresent && subTitlePresent) {
			msgSender.sendTitleAndSub(
					filteredRecipients,
					doPrefix ? ComponentUtil.combineComponents(prefixComponent, title) : title,
					doPrefixOnSub ? ComponentUtil.combineComponents(prefixComponent, subTitle) : subTitle
			);
		} else if (titlePresent) {
			msgSender.sendTitle(
					filteredRecipients,
					doPrefix ? ComponentUtil.combineComponents(prefixComponent, title) : title
			);
		} else if (subTitlePresent) {
			msgSender.sendSubTitle(
					filteredRecipients,
					doPrefixOnSub ? ComponentUtil.combineComponents(prefixComponent, subTitle) : subTitle
			);
		}
	}
}
