package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.Utils;
import com.booksaw.betterTeams.text.Formatter;
import com.booksaw.betterTeams.util.ComponentUtil;
import com.booksaw.betterTeams.util.StringUtil;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MessageService {

    @Getter
    private final MessageConfig messageConfig;

    private MessageSender messageSender;

    @Getter
    private Component prefixComponent;
    @Getter
    private String prefix;

    /**
     *
     * @param messageConfig The message configuration to use
     */
    public MessageService(@NotNull MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
        setPrefix("");
    }

    /**
     * Sets up the message sender with Adventure support.
     *
     * @param audiences The BukkitAudiences instance (null for legacy mode)
     */
    public void setupMessageSender(@Nullable BukkitAudiences audiences) {
        if (audiences != null) {
            this.messageSender = new AdventureMessageSender(audiences);
        } else {
            this.messageSender = new LegacyMessageSender();
        }
    }

    public void dumpMessageSender() {
        this.messageSender = null;
    }

    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
        this.prefixComponent = prefix.isEmpty() ? Component.empty() : Formatter.absolute().process(prefix);
    }

    public void loadPrefix(@NotNull String prefixKey) {
        String prefixValue = messageConfig.get(prefixKey);
        setPrefix(prefixValue);
    }

    /**
     * Gets a formatted message from the configuration.
     *
     * @param reference    The message key
     * @param replacements The placeholder replacements
     * @return The formatted message string
     */
    @NotNull
    public String getMessage(@NotNull String reference, @NotNull Object... replacements) {
        return messageConfig.get(reference, replacements);
    }

    /**
     * Gets a formatted message with PlaceholderAPI support.
     *
     * @param player       The player for PlaceholderAPI
     * @param reference    The message key
     * @param replacements The placeholder replacements
     * @return The formatted message string
     */
    @NotNull
    public String getMessage(@Nullable OfflinePlayer player, @NotNull String reference, @NotNull Object... replacements) {
        return messageConfig.get(player, reference, replacements);
    }


    /**
     * Sends a message to a recipient.
     *
     * @param recipient    The message recipient
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendMessage(@Nullable CommandSender recipient, @NotNull String reference, @NotNull Object... replacements) {
        sendMessage(recipient, true, reference, replacements);
    }

    /**
     * Sends a message to a recipient with optional prefix.
     *
     * @param recipient    The message recipient
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendMessage(@Nullable CommandSender recipient, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        if (recipient == null || messageSender == null) return;

        String message = (recipient instanceof Player p) ? getMessage(p, reference, replacements) : getMessage(reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendMessage(recipient, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a message to a recipient with a specific player context for PlaceholderAPI.
     *
     * @param recipient    The message recipient
     * @param player       The player for PlaceholderAPI context
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendMessage(@Nullable CommandSender recipient, @Nullable OfflinePlayer player, @NotNull String reference, @NotNull Object... replacements) {
        sendMessage(recipient, player, true, reference, replacements);
    }

    /**
     * Sends a message to a recipient with a specific player context and optional prefix.
     *
     * @param recipient    The message recipient
     * @param player       The player for PlaceholderAPI context
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendMessage(@Nullable CommandSender recipient, @Nullable OfflinePlayer player, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        if (recipient == null || messageSender == null) return;

        String message = getMessage(player, reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendMessage(recipient, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a message to multiple recipients.
     *
     * @param recipients   The message recipients
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendMessage(@NotNull Collection<? extends CommandSender> recipients, @NotNull String reference, @NotNull Object... replacements) {
        sendMessage(recipients, true, reference, replacements);
    }

    /**
     * Sends a message to multiple recipients with optional prefix.
     *
     * @param recipients   The message recipients
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendMessage(@NotNull Collection<? extends CommandSender> recipients, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        Collection<? extends CommandSender> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || messageSender == null) return;

        String message = getMessage(reference, replacements);
        if (message.isEmpty()) return;

        for (CommandSender recipient : filtered) {
            String pMessage;
            if (recipient instanceof Player player) {
                pMessage = StringUtil.setPlaceholders(player, message);
                if (pMessage.isEmpty()) continue;
            } else {
                pMessage = message;
            }

            Component component = Formatter.absolute().process(pMessage);
            messageSender.sendMessage(recipient, doPrefix ? combineWithPrefix(component) : component);
        }
    }

    /**
     * Sends a message to multiple recipients with a specific player context.
     *
     * @param recipients   The message recipients
     * @param player       The player for PlaceholderAPI context
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendMessage(@NotNull Collection<? extends CommandSender> recipients, @Nullable OfflinePlayer player, @NotNull String reference, @NotNull Object... replacements) {
        sendMessage(recipients, player, true, reference, replacements);
    }

    /**
     * Sends a message to multiple recipients with a specific player context and optional prefix.
     *
     * @param recipients   The message recipients
     * @param player       The player for PlaceholderAPI context
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendMessage(@NotNull Collection<? extends CommandSender> recipients, @Nullable OfflinePlayer player, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        Collection<? extends CommandSender> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || messageSender == null) return;

        String message = getMessage(player, reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendMessage(filtered, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a raw message string to a recipient.
     *
     * @param recipient The message recipient
     * @param message   The raw message string
     */
    public void sendFullMessage(@Nullable CommandSender recipient, @Nullable String message) {
        sendFullMessage(recipient, message, false);
    }

    /**
     * Sends a raw message string to a recipient with optional prefix.
     *
     * @param recipient The message recipient
     * @param message   The raw message string
     * @param doPrefix  Whether to include the prefix
     */
    public void sendFullMessage(@Nullable CommandSender recipient, @Nullable String message, boolean doPrefix) {
        if (recipient == null || message == null || message.isEmpty() || messageSender == null) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendMessage(recipient, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a raw message component to a recipient.
     *
     * @param recipient The message recipient
     * @param message   The message component
     */
    public void sendFullMessage(@Nullable CommandSender recipient, @Nullable Component message) {
        sendFullMessage(recipient, message, false);
    }


	/**
	 * Sends a formatted and sanitized message to a specified recipient.
	 *
	 * <p>This method MUST be used when handling any user-provided or untrusted input
	 * (e.g. player input, config values editable by players, database content, etc.).
	 * It ensures that unsafe MiniMessage tags such as click/hover events are not parsed,
	 * preventing abuse (e.g. command execution via <click:run_command>).</p>
	 *
	 * <p>If a prefix is required, it is combined with the processed message before sending.</p>
	 *
	 * @param recipient the intended recipient of the message; if null, the method does nothing
	 * @param message the raw message content; if null or empty, the method does nothing
	 * @param doPrefix whether the message should be prefixed before being sent
	 */
	public void sendSafeMessage(@Nullable CommandSender recipient, @Nullable String message, boolean doPrefix) {
		if (recipient == null || message == null || message.isEmpty() || messageSender == null) return;

		Component component = Formatter.safe().process(message);
		messageSender.sendMessage(recipient, doPrefix ? combineWithPrefix(component) : component);
	}

    /**
     * Sends a raw message component to a recipient with optional prefix.
     *
     * @param recipient The message recipient
     * @param message   The message component
     * @param doPrefix  Whether to include the prefix
     */
    public void sendFullMessage(@Nullable CommandSender recipient, @Nullable Component message, boolean doPrefix) {
        if (recipient == null || message == null || message.equals(Component.empty()) || messageSender == null) return;

        messageSender.sendMessage(recipient, doPrefix ? combineWithPrefix(message) : message);
    }

    /**
     * Sends a raw message string to multiple recipients.
     *
     * @param recipients The message recipients
     * @param message    The raw message string
     */
    public void sendFullMessage(@NotNull Collection<? extends CommandSender> recipients, @Nullable String message) {
        sendFullMessage(recipients, message, false);
    }

    /**
     * Sends a raw message string to multiple recipients with optional prefix.
     *
     * @param recipients The message recipients
     * @param message    The raw message string
     * @param doPrefix   Whether to include the prefix
     */
    public void sendFullMessage(@NotNull Collection<? extends CommandSender> recipients, @Nullable String message, boolean doPrefix) {
        Collection<? extends CommandSender> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || message == null || message.isEmpty() || messageSender == null) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendMessage(filtered, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a raw message component to multiple recipients.
     *
     * @param recipients The message recipients
     * @param message    The message component
     */
    public void sendFullMessage(@NotNull Collection<? extends CommandSender> recipients, @Nullable Component message) {
        sendFullMessage(recipients, message, false);
    }

    /**
     * Sends a raw message component to multiple recipients with optional prefix.
     *
     * @param recipients The message recipients
     * @param message    The message component
     * @param doPrefix   Whether to include the prefix
     */
    public void sendFullMessage(@NotNull Collection<? extends CommandSender> recipients, @Nullable Component message, boolean doPrefix) {
        Collection<? extends CommandSender> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || message == null || message.equals(Component.empty()) || messageSender == null) return;

        messageSender.sendMessage(filtered, doPrefix ? combineWithPrefix(message) : message);
    }

    /**
     * Sends a title to a player.
     *
     * @param recipient    The player to send to
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendTitle(@Nullable Player recipient, @NotNull String reference, @NotNull Object... replacements) {
        sendTitle(recipient, false, reference, replacements);
    }

    /**
     * Sends a title to a player with optional prefix.
     *
     * @param recipient    The player to send to
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendTitle(@Nullable Player recipient, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        if (recipient == null || messageSender == null) return;

        String message = getMessage(recipient, reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendTitle(recipient, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a title to a player with a specific player context for PlaceholderAPI.
     *
     * @param recipient    The player to send to
     * @param player       The player for PlaceholderAPI context
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendTitle(@Nullable Player recipient, @Nullable OfflinePlayer player, @NotNull String reference, @NotNull Object... replacements) {
        sendTitle(recipient, player, false, reference, replacements);
    }

    /**
     * Sends a title to a player with a specific player context and optional prefix.
     *
     * @param recipient    The player to send to
     * @param player       The player for PlaceholderAPI context
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendTitle(@Nullable Player recipient, @Nullable OfflinePlayer player, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        if (recipient == null || messageSender == null) return;

        String message = getMessage(player, reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendTitle(recipient, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a title to multiple players.
     *
     * @param recipients   The players to send to
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendTitle(@NotNull Collection<Player> recipients, @NotNull String reference, @NotNull Object... replacements) {
        sendTitle(recipients, false, reference, replacements);
    }

    /**
     * Sends a title to multiple players with optional prefix.
     *
     * @param recipients   The players to send to
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendTitle(@NotNull Collection<Player> recipients, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || messageSender == null) return;

        String message = getMessage(reference, replacements);
        if (message.isEmpty()) return;

        for (Player recipient : filtered) {
            String pMessage = StringUtil.setPlaceholders(recipient, message);
            if (pMessage.isEmpty()) continue;

            Component component = Formatter.absolute().process(pMessage);
            messageSender.sendTitle(recipient, doPrefix ? combineWithPrefix(component) : component);
        }
    }

    /**
     * Sends a title to multiple players with a specific player context.
     *
     * @param recipients   The players to send to
     * @param player       The player for PlaceholderAPI context
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendTitle(@NotNull Collection<Player> recipients, @Nullable OfflinePlayer player, @NotNull String reference, @NotNull Object... replacements) {
        sendTitle(recipients, player, false, reference, replacements);
    }

    /**
     * Sends a title to multiple players with a specific player context and optional prefix.
     *
     * @param recipients   The players to send to
     * @param player       The player for PlaceholderAPI context
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendTitle(@NotNull Collection<Player> recipients, @Nullable OfflinePlayer player, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || messageSender == null) return;

        String message = getMessage(player, reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendTitle(filtered, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a raw title string to a player.
     *
     * @param recipient The player to send to
     * @param message   The raw title string
     */
    public void sendFullTitle(@Nullable Player recipient, @Nullable String message) {
        sendFullTitle(recipient, message, false);
    }

    /**
     * Sends a raw title string to a player with optional prefix.
     *
     * @param recipient The player to send to
     * @param message   The raw title string
     * @param doPrefix  Whether to include the prefix
     */
    public void sendFullTitle(@Nullable Player recipient, @Nullable String message, boolean doPrefix) {
        if (recipient == null || message == null || message.isEmpty() || messageSender == null) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendTitle(recipient, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a raw title component to a player.
     *
     * @param recipient The player to send to
     * @param message   The title component
     */
    public void sendFullTitle(@Nullable Player recipient, @Nullable Component message) {
        sendFullTitle(recipient, message, false);
    }

    /**
     * Sends a raw title component to a player with optional prefix.
     *
     * @param recipient The player to send to
     * @param message   The title component
     * @param doPrefix  Whether to include the prefix
     */
    public void sendFullTitle(@Nullable Player recipient, @Nullable Component message, boolean doPrefix) {
        if (recipient == null || message == null || message.equals(Component.empty()) || messageSender == null) return;

        messageSender.sendTitle(recipient, doPrefix ? combineWithPrefix(message) : message);
    }

    /**
     * Sends a raw title string to multiple players.
     *
     * @param recipients The players to send to
     * @param message    The raw title string
     */
    public void sendFullTitle(@NotNull Collection<Player> recipients, @Nullable String message) {
        sendFullTitle(recipients, message, false);
    }

    /**
     * Sends a raw title string to multiple players with optional prefix.
     *
     * @param recipients The players to send to
     * @param message    The raw title string
     * @param doPrefix   Whether to include the prefix
     */
    public void sendFullTitle(@NotNull Collection<Player> recipients, @Nullable String message, boolean doPrefix) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || message == null || message.isEmpty() || messageSender == null) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendTitle(filtered, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a raw title component to multiple players.
     *
     * @param recipients The players to send to
     * @param message    The title component
     */
    public void sendFullTitle(@NotNull Collection<Player> recipients, @Nullable Component message) {
        sendFullTitle(recipients, message, false);
    }

    /**
     * Sends a raw title component to multiple players with optional prefix.
     *
     * @param recipients The players to send to
     * @param message    The title component
     * @param doPrefix   Whether to include the prefix
     */
    public void sendFullTitle(@NotNull Collection<Player> recipients, @Nullable Component message, boolean doPrefix) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || message == null || message.equals(Component.empty()) || messageSender == null) return;

        messageSender.sendTitle(filtered, doPrefix ? combineWithPrefix(message) : message);
    }

    /**
     * Sends a subtitle to a player.
     *
     * @param recipient    The player to send to
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendSubTitle(@Nullable Player recipient, @NotNull String reference, @NotNull Object... replacements) {
        sendSubTitle(recipient, false, reference, replacements);
    }

    /**
     * Sends a subtitle to a player with optional prefix.
     *
     * @param recipient    The player to send to
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendSubTitle(@Nullable Player recipient, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        if (recipient == null || messageSender == null) return;

        String message = getMessage(recipient, reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendSubTitle(recipient, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a subtitle to a player with a specific player context for PlaceholderAPI.
     *
     * @param recipient    The player to send to
     * @param player       The player for PlaceholderAPI context
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendSubTitle(@Nullable Player recipient, @Nullable OfflinePlayer player, @NotNull String reference, @NotNull Object... replacements) {
        sendSubTitle(recipient, player, false, reference, replacements);
    }

    /**
     * Sends a subtitle to a player with a specific player context and optional prefix.
     *
     * @param recipient    The player to send to
     * @param player       The player for PlaceholderAPI context
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendSubTitle(@Nullable Player recipient, @Nullable OfflinePlayer player, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        if (recipient == null || messageSender == null) return;

        String message = getMessage(player, reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendSubTitle(recipient, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a subtitle to multiple players.
     *
     * @param recipients   The players to send to
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendSubTitle(@NotNull Collection<Player> recipients, @NotNull String reference, @NotNull Object... replacements) {
        sendSubTitle(recipients, false, reference, replacements);
    }

    /**
     * Sends a subtitle to multiple players with optional prefix.
     *
     * @param recipients   The players to send to
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendSubTitle(@NotNull Collection<Player> recipients, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || messageSender == null) return;

        String message = getMessage(reference, replacements);
        if (message.isEmpty()) return;

        for (Player recipient : filtered) {
            String pMessage = StringUtil.setPlaceholders(recipient, message);
            if (pMessage.isEmpty()) continue;

            Component component = Formatter.absolute().process(pMessage);
            messageSender.sendSubTitle(recipient, doPrefix ? combineWithPrefix(component) : component);
        }
    }

    /**
     * Sends a subtitle to multiple players with a specific player context.
     *
     * @param recipients   The players to send to
     * @param player       The player for PlaceholderAPI context
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendSubTitle(@NotNull Collection<Player> recipients, @Nullable OfflinePlayer player, @NotNull String reference, @NotNull Object... replacements) {
        sendSubTitle(recipients, player, false, reference, replacements);
    }

    /**
     * Sends a subtitle to multiple players with a specific player context and optional prefix.
     *
     * @param recipients   The players to send to
     * @param player       The player for PlaceholderAPI context
     * @param doPrefix     Whether to include the prefix
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendSubTitle(@NotNull Collection<Player> recipients, @Nullable OfflinePlayer player, boolean doPrefix, @NotNull String reference, @NotNull Object... replacements) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || messageSender == null) return;

        String message = getMessage(player, reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendSubTitle(filtered, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a raw subtitle string to a player.
     *
     * @param recipient The player to send to
     * @param message   The raw subtitle string
     */
    public void sendFullSubTitle(@Nullable Player recipient, @Nullable String message) {
        sendFullSubTitle(recipient, message, false);
    }

    /**
     * Sends a raw subtitle string to a player with optional prefix.
     *
     * @param recipient The player to send to
     * @param message   The raw subtitle string
     * @param doPrefix  Whether to include the prefix
     */
    public void sendFullSubTitle(@Nullable Player recipient, @Nullable String message, boolean doPrefix) {
        if (recipient == null || message == null || message.isEmpty() || messageSender == null) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendSubTitle(recipient, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a raw subtitle component to a player.
     *
     * @param recipient The player to send to
     * @param message   The subtitle component
     */
    public void sendFullSubTitle(@Nullable Player recipient, @Nullable Component message) {
        sendFullSubTitle(recipient, message, false);
    }

    /**
     * Sends a raw subtitle component to a player with optional prefix.
     *
     * @param recipient The player to send to
     * @param message   The subtitle component
     * @param doPrefix  Whether to include the prefix
     */
    public void sendFullSubTitle(@Nullable Player recipient, @Nullable Component message, boolean doPrefix) {
        if (recipient == null || message == null || message.equals(Component.empty()) || messageSender == null) return;

        messageSender.sendSubTitle(recipient, doPrefix ? combineWithPrefix(message) : message);
    }

    /**
     * Sends a raw subtitle string to multiple players.
     *
     * @param recipients The players to send to
     * @param message    The raw subtitle string
     */
    public void sendFullSubTitle(@NotNull Collection<Player> recipients, @Nullable String message) {
        sendFullSubTitle(recipients, message, false);
    }

    /**
     * Sends a raw subtitle string to multiple players with optional prefix.
     *
     * @param recipients The players to send to
     * @param message    The raw subtitle string
     * @param doPrefix   Whether to include the prefix
     */
    public void sendFullSubTitle(@NotNull Collection<Player> recipients, @Nullable String message, boolean doPrefix) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || message == null || message.isEmpty() || messageSender == null) return;

        Component component = Formatter.absolute().process(message);
        messageSender.sendSubTitle(filtered, doPrefix ? combineWithPrefix(component) : component);
    }

    /**
     * Sends a raw subtitle component to multiple players.
     *
     * @param recipients The players to send to
     * @param message    The subtitle component
     */
    public void sendFullSubTitle(@NotNull Collection<Player> recipients, @Nullable Component message) {
        sendFullSubTitle(recipients, message, false);
    }

    /**
     * Sends a raw subtitle component to multiple players with optional prefix.
     *
     * @param recipients The players to send to
     * @param message    The subtitle component
     * @param doPrefix   Whether to include the prefix
     */
    public void sendFullSubTitle(@NotNull Collection<Player> recipients, @Nullable Component message, boolean doPrefix) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || message == null || message.equals(Component.empty()) || messageSender == null) return;

        messageSender.sendSubTitle(filtered, doPrefix ? combineWithPrefix(message) : message);
    }

    /**
     * Sends both a title and subtitle to a player.
     *
     * @param recipient The player to send to
     * @param title     The title string
     * @param subTitle  The subtitle string
     */
    public void sendFullTitleAndSub(@Nullable Player recipient, @Nullable String title, @Nullable String subTitle) {
        sendFullTitleAndSub(recipient, title, subTitle, false, false);
    }

    /**
     * Sends both a title and subtitle to a player with optional prefixes.
     *
     * @param recipient      The player to send to
     * @param title          The title string
     * @param subTitle       The subtitle string
     * @param doPrefix       Whether to include prefix on title
     * @param doPrefixOnSub  Whether to include prefix on subtitle
     */
    public void sendFullTitleAndSub(@Nullable Player recipient, @Nullable String title, @Nullable String subTitle, boolean doPrefix, boolean doPrefixOnSub) {
        if (recipient == null || messageSender == null) return;

        boolean titlePresent = title != null && !title.isEmpty();
        boolean subTitlePresent = subTitle != null && !subTitle.isEmpty();

        if (titlePresent && subTitlePresent) {
            Component titleComponent = Formatter.absolute().process(title);
            Component subTitleComponent = Formatter.absolute().process(subTitle);

            messageSender.sendTitleAndSub(
                    recipient,
                    doPrefix ? combineWithPrefix(titleComponent) : titleComponent,
                    doPrefixOnSub ? combineWithPrefix(subTitleComponent) : subTitleComponent
            );
        } else if (titlePresent) {
            Component titleComponent = Formatter.absolute().process(title);
            messageSender.sendTitle(recipient, doPrefix ? combineWithPrefix(titleComponent) : titleComponent);
        } else if (subTitlePresent) {
            Component subTitleComponent = Formatter.absolute().process(subTitle);
            messageSender.sendSubTitle(recipient, doPrefixOnSub ? combineWithPrefix(subTitleComponent) : subTitleComponent);
        }
    }

    /**
     * Sends both a title and subtitle to multiple players.
     *
     * @param recipients The players to send to
     * @param title      The title string
     * @param subTitle   The subtitle string
     */
    public void sendFullTitleAndSub(@NotNull Collection<Player> recipients, @Nullable String title, @Nullable String subTitle) {
        sendFullTitleAndSub(recipients, title, subTitle, false, false);
    }

    /**
     * Sends both a title and subtitle to multiple players with optional prefixes.
     *
     * @param recipients     The players to send to
     * @param title          The title string
     * @param subTitle       The subtitle string
     * @param doPrefix       Whether to include prefix on title
     * @param doPrefixOnSub  Whether to include prefix on subtitle
     */
    public void sendFullTitleAndSub(@NotNull Collection<Player> recipients, @Nullable String title, @Nullable String subTitle, boolean doPrefix, boolean doPrefixOnSub) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || messageSender == null) return;

        boolean titlePresent = title != null && !title.isEmpty();
        boolean subTitlePresent = subTitle != null && !subTitle.isEmpty();

        if (titlePresent && subTitlePresent) {
            Component titleComponent = Formatter.absolute().process(title);
            Component subTitleComponent = Formatter.absolute().process(subTitle);

            messageSender.sendTitleAndSub(
                    filtered,
                    doPrefix ? combineWithPrefix(titleComponent) : titleComponent,
                    doPrefixOnSub ? combineWithPrefix(subTitleComponent) : subTitleComponent
            );
        } else if (titlePresent) {
            Component titleComponent = Formatter.absolute().process(title);
            messageSender.sendTitle(filtered, doPrefix ? combineWithPrefix(titleComponent) : titleComponent);
        } else if (subTitlePresent) {
            Component subTitleComponent = Formatter.absolute().process(subTitle);
            messageSender.sendSubTitle(filtered, doPrefixOnSub ? combineWithPrefix(subTitleComponent) : subTitleComponent);
        }
    }

    /**
     * Sends both a title and subtitle component to a player.
     *
     * @param recipient The player to send to
     * @param title     The title component
     * @param subTitle  The subtitle component
     */
    public void sendFullTitleAndSub(@Nullable Player recipient, @Nullable Component title, @Nullable Component subTitle) {
        sendFullTitleAndSub(recipient, title, subTitle, false, false);
    }

    /**
     * Sends both a title and subtitle component to a player with optional prefixes.
     *
     * @param recipient      The player to send to
     * @param title          The title component
     * @param subTitle       The subtitle component
     * @param doPrefix       Whether to include prefix on title
     * @param doPrefixOnSub  Whether to include prefix on subtitle
     */
    public void sendFullTitleAndSub(@Nullable Player recipient, @Nullable Component title, @Nullable Component subTitle, boolean doPrefix, boolean doPrefixOnSub) {
        if (recipient == null || messageSender == null) return;

        boolean titlePresent = title != null && !title.equals(Component.empty());
        boolean subTitlePresent = subTitle != null && !subTitle.equals(Component.empty());

        if (titlePresent && subTitlePresent) {
            messageSender.sendTitleAndSub(
                    recipient,
                    doPrefix ? combineWithPrefix(title) : title,
                    doPrefixOnSub ? combineWithPrefix(subTitle) : subTitle
            );
        } else if (titlePresent) {
            messageSender.sendTitle(recipient, doPrefix ? combineWithPrefix(title) : title);
        } else if (subTitlePresent) {
            messageSender.sendSubTitle(recipient, doPrefixOnSub ? combineWithPrefix(subTitle) : subTitle);
        }
    }

    /**
     * Sends both a title and subtitle component to multiple players.
     *
     * @param recipients The players to send to
     * @param title      The title component
     * @param subTitle   The subtitle component
     */
    public void sendFullTitleAndSub(@NotNull Collection<Player> recipients, @Nullable Component title, @Nullable Component subTitle) {
        sendFullTitleAndSub(recipients, title, subTitle, false, false);
    }

    /**
     * Sends both a title and subtitle component to multiple players with optional prefixes.
     *
     * @param recipients     The players to send to
     * @param title          The title component
     * @param subTitle       The subtitle component
     * @param doPrefix       Whether to include prefix on title
     * @param doPrefixOnSub  Whether to include prefix on subtitle
     */
    public void sendFullTitleAndSub(@NotNull Collection<Player> recipients, @Nullable Component title, @Nullable Component subTitle, boolean doPrefix, boolean doPrefixOnSub) {
        Collection<Player> filtered = Utils.filterNonNull(recipients);
        if (filtered.isEmpty() || messageSender == null) return;

        boolean titlePresent = title != null && !title.equals(Component.empty());
        boolean subTitlePresent = subTitle != null && !subTitle.equals(Component.empty());

        if (titlePresent && subTitlePresent) {
            messageSender.sendTitleAndSub(
                    filtered,
                    doPrefix ? combineWithPrefix(title) : title,
                    doPrefixOnSub ? combineWithPrefix(subTitle) : subTitle
            );
        } else if (titlePresent) {
            messageSender.sendTitle(filtered, doPrefix ? combineWithPrefix(title) : title);
        } else if (subTitlePresent) {
            messageSender.sendSubTitle(filtered, doPrefixOnSub ? combineWithPrefix(subTitle) : subTitle);
        }
    }

    /**
     * Sends an action bar message to a player.
     *
     * @param recipient    The player to send to
     * @param reference    The message key
     * @param replacements The placeholder replacements
     */
    public void sendActionBar(@Nullable Player recipient, @NotNull String reference, @NotNull Object... replacements) {
        if (recipient == null || messageSender == null) return;

        String message = getMessage(recipient, reference, replacements);
        if (message.isEmpty()) return;

        Component component = Formatter.absolute().process(message);

        // Action bar doesn't typically use prefix, but we can add if needed
        if (messageSender instanceof AdventureMessageSender adventureSender) {
            adventureSender.sendActionBar(recipient, component);
        }
    }

    /**
     * Sends a raw action bar message to a player.
     *
     * @param recipient The player to send to
     * @param message   The raw action bar string
     */
    public void sendFullActionBar(@Nullable Player recipient, @Nullable String message) {
        if (recipient == null || message == null || message.isEmpty() || messageSender == null) return;

        Component component = Formatter.absolute().process(message);

        if (messageSender instanceof AdventureMessageSender adventureSender) {
            adventureSender.sendActionBar(recipient, component);
        }
    }

    @NotNull
    private Component combineWithPrefix(@NotNull Component component) {
        return ComponentUtil.combineComponents(prefixComponent, component);
    }

    public void reload() {
        messageConfig.reload();
    }

    public void reload(@NotNull String language) {
        messageConfig.reload(language);
    }
}