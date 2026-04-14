package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.Main;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;

/**
 * Used to control all communications to the user
 *
 * @author booksaw
 */
public class MessageManager {

	@Getter
	private static MessageService mainPluginService;

	private static BukkitAudiences audiences;

	/**
	 * Stopping this class being instantiated
	 */
	private MessageManager() {
	}

	public static void addMessages(@NotNull String lang) {
		MessageConfig config = new MessageConfig(lang);
		mainPluginService = new MessageService(config);

		String prefix = Main.plugin.getConfig().getString("prefixFormat", "");
		mainPluginService.setPrefix(prefix);
		mainPluginService.setupMessageSender(audiences);
	}

	/**
	 * Used to instantiate a new MessageSender.
	 * <p>
	 * Warning: This is not API, so it should never be used outside BetterTeams' package
	 */
	@Internal
	public static void setupMessageSender(BukkitAudiences audiences) {
		MessageManager.audiences = audiences;
		if (mainPluginService != null) {
			mainPluginService.setupMessageSender(audiences);
		}
	}

	public static void addBackupMessages(YamlConfiguration file) {
		if (mainPluginService != null) {
			mainPluginService.getMessageConfig().loadBackupMessages(file);
		}
	}

	public static void reload() {
		if(mainPluginService != null) {
			mainPluginService.reload();
		}
	}

	public static String getLanguage() {
		return mainPluginService.getMessageConfig().getLanguage();
	}

	public static String getPrefix() {
		return mainPluginService == null ? "" : getPrefixComponent().insertion();
	}

	public static Component getPrefixComponent() {
		return mainPluginService == null ? Component.empty() : mainPluginService.getPrefixComponent();
	}

	public static @NotNull String getMessage(String reference) {
		return mainPluginService.getMessage(reference);
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
		return mainPluginService.getMessage(reference, replacements);
	}

	public static @NotNull String getMessage(OfflinePlayer player, String reference, Object... replacements) {
		return mainPluginService.getMessage(player, reference, replacements);
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
		mainPluginService.sendMessage(recipient, doPrefix, reference, replacements);
	}

	public static void sendMessage(CommandSender recipient, @Nullable OfflinePlayer player, String reference, Object... replacements) {
		sendMessage(recipient, player, true, reference, replacements);
	}

	public static void sendMessage(CommandSender recipient, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		mainPluginService.sendMessage(recipient, player, doPrefix, reference, replacements);
	}

	public static void sendMessage(Collection<? extends CommandSender> recipients, String reference, Object... replacements) {
		sendMessage(recipients, true, reference, replacements);
	}

	public static void sendMessage(Collection<? extends CommandSender> recipients, boolean doPrefix, String reference, Object... replacements) {
		mainPluginService.sendMessage(recipients, doPrefix, reference, replacements);
	}

	public static void sendMessage(Collection<? extends CommandSender> recipients, @Nullable OfflinePlayer player, String reference, Object... replacements) {
		sendMessage(recipients, player, true, reference, replacements);
	}

	public static void sendMessage(Collection<? extends CommandSender> recipients, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		mainPluginService.sendMessage(recipients, player, doPrefix, reference, replacements);
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
		mainPluginService.sendFullMessage(recipient, message, doPrefix);
	}

	public static void sendFullMessage(Collection<? extends CommandSender> senders, String message) {
		sendFullMessage(senders, message, false);
	}

	public static void sendSafeMessage(CommandSender recipient, String message, boolean doPrefix) {
		mainPluginService.sendSafeMessage(recipient, message, doPrefix);
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
		mainPluginService.sendFullMessage(recipients, message, doPrefix);
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
		mainPluginService.sendFullMessage(recipient, message, doPrefix);
	}

	public static void sendFullMessage(Collection<? extends CommandSender> recipients, Component message) {
		sendFullMessage(recipients, message, false);
	}

	public static void sendFullMessage(Collection<? extends CommandSender> recipients, Component message, boolean doPrefix) {
		mainPluginService.sendFullMessage(recipients, message, doPrefix);
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
		mainPluginService.sendTitle(recipient, doPrefix, reference, replacements);
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
		mainPluginService.sendTitle(recipient, player, doPrefix, reference, replacements);
	}

	public static void sendTitle(Collection<Player> recipients, String reference, Object... replacements) {
		sendTitle(recipients, false, reference, replacements);
	}

	public static void sendTitle(Collection<Player> recipients, boolean doPrefix, String reference, Object... replacements) {
		mainPluginService.sendTitle(recipients, doPrefix, reference, replacements);
	}

	public static void sendTitle(Collection<Player> recipients, @Nullable OfflinePlayer player, String reference, Object... replacements) {
		sendTitle(recipients, player, false, reference, replacements);
	}

	public static void sendTitle(Collection<Player> recipients, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		mainPluginService.sendTitle(recipients, player, doPrefix, reference, replacements);
	}

	public static void sendFullTitle(Player recipient, String message) {
		sendFullTitle(recipient, message, false);
	}

	public static void sendFullTitle(Player recipient, String message, boolean doPrefix) {
		mainPluginService.sendFullTitle(recipient, message, doPrefix);
	}

	public static void sendFullTitle(Collection<Player> recipients, String message) {
		sendFullTitle(recipients, message, false);
	}

	public static void sendFullTitle(Collection<Player> recipients, String message, boolean doPrefix) {
		mainPluginService.sendFullTitle(recipients, message, doPrefix);
	}

	public static void sendFullTitle(Player recipient, Component message) {
		sendFullTitle(recipient, message, false);
	}

	public static void sendFullTitle(Player recipient, Component message, boolean doPrefix) {
		mainPluginService.sendFullTitle(recipient, message, doPrefix);
	}

	public static void sendFullTitle(Collection<Player> recipients, Component message) {
		sendFullTitle(recipients, message, false);
	}

	public static void sendFullTitle(Collection<Player> recipients, Component message, boolean doPrefix) {
		mainPluginService.sendFullTitle(recipients, message, doPrefix);
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
		mainPluginService.sendSubTitle(recipient, doPrefix, reference, replacements);
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
		mainPluginService.sendSubTitle(recipient, player, doPrefix, reference, replacements);
	}

	public static void sendSubTitle(Collection<Player> recipients, String reference, Object... replacements) {
		sendSubTitle(recipients, false, reference, replacements);
	}

	public static void sendSubTitle(Collection<Player> recipients, boolean doPrefix, String reference, Object... replacements) {
		mainPluginService.sendSubTitle(recipients, doPrefix, reference, replacements);
	}

	public static void sendSubTitle(Collection<Player> recipients, @Nullable OfflinePlayer player, String reference, Object... replacements) {
		sendSubTitle(recipients, player, false, reference, replacements);
	}

	public static void sendSubTitle(Collection<Player> recipients, @Nullable OfflinePlayer player, boolean doPrefix, String reference, Object... replacements) {
		mainPluginService.sendSubTitle(recipients, player, doPrefix, reference, replacements);
	}

	public static void sendFullSubTitle(Player recipient, String message) {
		sendFullSubTitle(recipient, message, false);
	}

	public static void sendFullSubTitle(Player recipient, String message, boolean doPrefix) {
		mainPluginService.sendFullSubTitle(recipient, message, doPrefix);
	}

	public static void sendFullSubTitle(Collection<Player> recipients, String message) {
		sendFullSubTitle(recipients, message, false);
	}

	public static void sendFullSubTitle(Collection<Player> recipients, String message, boolean doPrefix) {
		mainPluginService.sendFullSubTitle(recipients, message, doPrefix);
	}

	public static void sendFullSubTitle(Player recipient, Component message) {
		sendFullSubTitle(recipient, message, false);
	}

	public static void sendFullSubTitle(Player recipient, Component message, boolean doPrefix) {
		mainPluginService.sendFullSubTitle(recipient, message, doPrefix);
	}

	public static void sendFullSubTitle(Collection<Player> recipients, Component message) {
		sendFullSubTitle(recipients, message, false);
	}

	public static void sendFullSubTitle(Collection<Player> recipients, Component message, boolean doPrefix) {
		mainPluginService.sendFullSubTitle(recipients, message, doPrefix);
	}

	public static void sendFullTitleAndSub(Player recipient, String title, String subTitle) {
		sendFullTitleAndSub(recipient, title, subTitle, false, false);
	}

	public static void sendFullTitleAndSub(Player recipient, String title, String subTitle, boolean doPrefix, boolean doPrefixOnSub) {
		mainPluginService.sendFullTitleAndSub(recipient, title, subTitle, doPrefix, doPrefixOnSub);
	}

	public static void sendFullTitleAndSub(Collection<Player> recipients, String title, String subTitle) {
		sendFullTitleAndSub(recipients, title, subTitle, false, false);
	}

	public static void sendFullTitleAndSub(Collection<Player> recipients, String title, String subTitle, boolean doPrefix, boolean doPrefixOnSub) {
		mainPluginService.sendFullTitleAndSub(recipients, title, subTitle, doPrefix, doPrefixOnSub);
	}

	public static void sendFullTitleAndSub(Player recipient, Component title, Component subTitle) {
		sendFullTitleAndSub(recipient, title, subTitle, false, false);
	}

	public static void sendFullTitleAndSub(Player recipient, Component title, Component subTitle, boolean doPrefix, boolean doPrefixOnSub) {
		mainPluginService.sendFullTitleAndSub(recipient, title, subTitle, doPrefix, doPrefixOnSub);
	}

	public static void sendFullTitleAndSub(Collection<Player> recipients, Component title, Component subTitle) {
		sendFullTitleAndSub(recipients, title, subTitle, false, false);
	}

	public static void sendFullTitleAndSub(Collection<Player> recipients, Component title, Component subTitle, boolean doPrefix, boolean doPrefixOnSub) {
		mainPluginService.sendFullTitleAndSub(recipients, title, subTitle, doPrefix, doPrefixOnSub);
	}

	public static @NotNull FileConfiguration getDefaultMessages() {
		return mainPluginService.getMessageConfig().getConfig();
	}

	public static File getFile() {
		return mainPluginService.getMessageConfig().getFile();
	}

	public static MessageConfig.MessageBuilder builder(String path) {
		return mainPluginService.getMessageConfig().builder(path);
	}
}