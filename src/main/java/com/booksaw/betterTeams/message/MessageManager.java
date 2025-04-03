package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.ConfigManager;
import com.booksaw.betterTeams.Main;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to control all communications to the user
 *
 * @author booksaw
 */
public class MessageManager {

	public static final String MISSINGMESSAGES_FILENAME = "missingmessages.txt";

	private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
	private static final Pattern LEGACY_TAG_PATTERN = Pattern.compile("[§&]([0-9a-fk-orxA-FK-ORX])");
	private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");

	private static final MiniMessage miniMessage = MiniMessage.miniMessage();
	private static BukkitAudiences adventure;

	/**
	 * Used to store all loaded messages
	 */
	@Getter
	private static HashMap<String, String> messages = new HashMap<>();

	/**
	 * Used to store all loaded messages in MiniMessage format
	 */
	@Getter
	private static HashMap<String, Component> miniMessages = new HashMap<>();

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

	public static void initAdventure() {
		if (adventure != null) {
			return;
		}
		adventure = BukkitAudiences.create(Main.plugin);
	}

	public static boolean isAdventure() {
		return adventure != null;
	}

	public static String getLanguage() {
		return lang;
	}

	public static void setLanguage(String lang) {
		MessageManager.lang = lang;
	}

	/**
	 * Translates RGB color codes (&#RRGGBB) into Minecraft color codes.
	 *
	 * @param message The message to translate.
	 * @return The translated message with RGB colors applied.
	 */
	public static String translateRGBColors(String message) {
		if (message == null || message.isEmpty()) {
			return message;
		}

		Matcher matcher = HEX_PATTERN.matcher(message);

		StringBuffer translatedMessage = new StringBuffer();

		while (matcher.find()) {
			String hexColor = matcher.group(1); // Extract the RRGGBB part
			String minecraftColor = ChatColor.of("#" + hexColor).toString(); // Convert to Minecraft color
			matcher.appendReplacement(translatedMessage, minecraftColor);
		}

		matcher.appendTail(translatedMessage);
		return translatedMessage.toString();
	}
	
	/**
	 * Transforms legacy formatting codes (§ or &) and Minecraft's hex color format (§x§R§R§G§G§B§B)
	 * into MiniMessage-compatible tags.
	 *
	 * @param message The message to transform.
	 * @return The transformed message with MiniMessage-compatible tags.
	 */
	public static String transformLegacyToMiniMessage(String message) {
		if (message == null || message.isEmpty()) {
			return message;
		}

		// Convert Minecraft's hex color format (§x§R§R§G§G§B§B) to MiniMessage format (<color:#RRGGBB>)
		Matcher hexMatcher = LEGACY_HEX_PATTERN.matcher(message);
		StringBuffer convertedMessage = new StringBuffer();

		while (hexMatcher.find()) {
			// Extract the hex color from the matched format
			String hexColor = hexMatcher.group().replace("§x", "").replace("§", "");
			hexMatcher.appendReplacement(convertedMessage, "<color:#" + hexColor + ">");
		}
		hexMatcher.appendTail(convertedMessage);

		// Update the message with the converted hex colors
		message = convertedMessage.toString();

		Matcher legacyMatcher = LEGACY_TAG_PATTERN.matcher(message);
		convertedMessage = new StringBuffer();

		while (legacyMatcher.find()) {
			char code = legacyMatcher.group(1).toLowerCase().charAt(0); // Get the character after § or &
			ChatColor chatColor = ChatColor.getByChar(code); // Get the ChatColor associated with the code

			if (chatColor != null) {
				String miniMessageTag = "<" + chatColor.getName() + ">"; // Get the MiniMessage tag
				legacyMatcher.appendReplacement(convertedMessage, miniMessageTag);
			}
		}
		legacyMatcher.appendTail(convertedMessage);

		return convertedMessage.toString();
	}

	/**
	 * Formats a message using MiniMessage.
	 *
	 * @param message The message to format.
	 * @return The formatted Component.
	 */
	public static Component formatWithMiniMessage(String message) {
		if (message == null || message.isEmpty()) {
			return Component.empty();
		}
		return miniMessage.deserialize(message);
	}

	/**
	 * This method is used to provide the configuration file in which all the
	 * message references are stored, this method also loads the default prefix
	 *
	 * @param configManager the configuration manager
	 */
	public static void addMessages(@NotNull ConfigManager configManager) {
		prefix = ChatColor.translateAlternateColorCodes('&',translateRGBColors(
				Objects.requireNonNull(Main.plugin.getConfig().getString("prefixFormat"))));
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

		String message = getMessage(sender, reference, replacement);
		if (message.isEmpty()) {
			return;
		}

		sendFullMessage(sender, message, true);
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
				Main.plugin.getLogger().warning("Could not find the message with the reference " + reference);
				return "";
			}

			String msg = messages.get(reference);
			if (msg.isEmpty()) {
				return "";
			}

			msg = format(msg, replacement);

			return msg;
		} catch (NullPointerException e) {
			Main.plugin.getLogger().warning("Could not find the message with the reference " + reference);
			return "";
		}
	}

	public static String getMessage(@Nullable CommandSender sender, String reference, Object... replacement) {
		try {
			String msg = getMessage(reference, replacement);
			if (msg.isEmpty()) {
				return "";
			}

			if (sender instanceof Player && Main.placeholderAPI) {
				msg = PlaceholderAPI.setPlaceholders((Player) sender, msg);
			}
			return ChatColor.translateAlternateColorCodes('&', translateRGBColors(msg));
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
		if (sender == null) {
			return;
		}

		if (prefixMessage) {
			message = prefix + message;
		}

		// Determine the formatting method based on the sender type
		if (sender instanceof Player) {
			// For players, use MiniMessage for advanced formatting
			if (adventure != null) {
				adventure.sender(sender).sendMessage(
						formatWithMiniMessage(transformLegacyToMiniMessage(message)));
			} else {
				sender.sendMessage(message);
			}
		} else {
			// For console or other senders
			sender.sendMessage(message);
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

	/**
	 * Used to send a (formatted) title to the specified user
	 *
	 * @param player      the commandSender which the message should be sent to
	 * @param reference   the reference for the message
	 * @param replacement the value that the placeholder should be replaced with
	 */
	public static void sendTitle(@Nullable Player player, String reference, Object... replacement) {
		String message = getMessage(player, reference, replacement);
		sendFullTitle(player, message, false);
	}

	public static void sendFullTitle(@Nullable Player player, String message) {
		sendFullTitle(player, message, true);
	}

	public static void sendFullTitle(@Nullable Player player, String message, boolean prefixMessage) {
		if (player == null) {
			return;
		}

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
