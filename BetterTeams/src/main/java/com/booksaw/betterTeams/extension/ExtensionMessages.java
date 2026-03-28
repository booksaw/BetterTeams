package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.message.MessageConfig;
import com.booksaw.betterTeams.message.MessageService;
import com.booksaw.betterTeams.message.StaticMessage;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Getter
public class ExtensionMessages {

    private final MessageService messageService;
    private final BetterTeamsExtension extension;
    private String fileName;

    public ExtensionMessages(@NotNull BetterTeamsExtension extension, @NotNull String fileName) {
        this.extension = extension;
        this.fileName = fileName;

        MessageConfig config = new MessageConfig(fileName, extension);

        this.messageService = new MessageService(config);

        try {
            BukkitAudiences audiences = Main.plugin.getAdventure();
            this.messageService.setupMessageSender(audiences);
        } catch (Exception e) {
            this.messageService.setupMessageSender(null);
        }

        if (config.has("prefix")) {
            this.messageService.loadPrefix("prefix");
        } else {
            this.messageService.setPrefix(extension.getInfo().getName());
        }
    }

    /**
     * Reloads messages from a different file
     * @param fileName The new file name (without .yml extension)
     */
    public void reload(@NotNull String fileName) {
        if (fileName.isEmpty()) throw new IllegalArgumentException("File name cannot be empty");
        this.fileName = fileName;
        this.messageService.reload(fileName);

        if (has("prefix")) {
            this.messageService.loadPrefix("prefix");
        }
    }

    public void reload() {
        reload(this.fileName);
    }

    /**
     * Gets a raw string from config with color codes translated
     *
     * @param path The path in messages file
     * @return The formatted message, or an empty string if not found
     */
    @NotNull
    public String get(@NotNull String path) {
        return messageService.getMessage(path);
    }

    /**
     * Gets a message with placeholders replaced
     */
    @NotNull
    public String get(@NotNull String path, @NotNull Object... replacements) {
        return messageService.getMessage(path, replacements);
    }

    @NotNull
    public String getPrefix() {
        return messageService.getPrefix();
    }

    @NotNull
    public String getWithPrefix(@NotNull String path) {
        return getPrefix() + get(path);
    }

    /**
     * Gets a message with the prefix and placeholders replaced
     */
    @NotNull
    public String getWithPrefix(@NotNull String path, @NotNull Object... replacements) {
        return getPrefix() + get(path, replacements);
    }

    /**
     * Creates a StaticMessage for use in BetterTeams commands
     * Includes the prefix
     *
     * @param path The message path
     * @return A StaticMessage instance
     */
    @NotNull
    public StaticMessage toStatic(@NotNull String path) {
        return new StaticMessage(getWithPrefix(path));
    }

    /**
     * Creates a StaticMessage with placeholders replaced
     * Includes the prefix
     *
     * @param path          The message path
     * @param replacements Pairs of key-value replacements
     * @return A StaticMessage instance
     */
    @NotNull
    public StaticMessage toStatic(@NotNull String path, @NotNull Object... replacements) {
        return new StaticMessage(getWithPrefix(path, replacements));
    }

    /**
     * Creates a StaticMessage without prefix
     */
    @NotNull
    public StaticMessage toStaticRaw(@NotNull String path) {
        return new StaticMessage(get(path));
    }

    /**
     * Creates a StaticMessage without prefix but with placeholders
     */
    @NotNull
    public StaticMessage toStaticRaw(@NotNull String path, @NotNull Object... replacements) {
        return new StaticMessage(get(path, replacements));
    }

    /**
     * Creates a CommandResponse with custom success state
     * Includes the prefix
     *
     * @param success Whether the command was successful
     * @param path    The message path
     * @return A CommandResponse instance
     */
    @NotNull
    public CommandResponse response(boolean success, @NotNull String path) {
        return new CommandResponse(success, toStatic(path));
    }

    /**
     * Creates a CommandResponse with custom success state and placeholders
     * Includes the prefix
     *
     * @param success       Whether the command was successful
     * @param path          The message path
     * @param replacements Pairs of key-value replacements
     * @return A CommandResponse instance
     */
    @NotNull
    public CommandResponse response(boolean success, @NotNull String path, @NotNull Object... replacements) {
        return new CommandResponse(success, toStatic(path, replacements));
    }

    /**
     * Sends a message directly to a CommandSender with prefix
     *
     * @param sender The recipient
     * @param path   The message path
     */
    public void send(@NotNull CommandSender sender, @NotNull String path) {
        messageService.sendMessage(sender, true, path);
    }

    /**
     * Sends a message directly to a CommandSender with prefix and placeholders
     *
     * @param sender       The recipient
     * @param path         The message path
     * @param replacements Pairs of key-value replacements
     */
    public void send(@NotNull CommandSender sender, @NotNull String path, @NotNull Object... replacements) {
        messageService.sendMessage(sender, true, path, replacements);
    }

    /**
     * Sends a message directly to multiple CommandSenders with prefix
     *
     * @param recipients The recipients
     * @param path       The message path
     */
    public void send(@NotNull Collection<? extends CommandSender> recipients, @NotNull String path) {
        messageService.sendMessage(recipients, true, path);
    }

    /**
     * Sends a message directly to multiple CommandSenders with prefix and placeholders
     *
     * @param recipients   The recipients
     * @param path         The message path
     * @param replacements Pairs of key-value replacements
     */
    public void send(@NotNull Collection<? extends CommandSender> recipients, @NotNull String path, @NotNull Object... replacements) {
        messageService.sendMessage(recipients, true, path, replacements);
    }

    /**
     * Sends a raw message (no prefix) directly to a CommandSender
     *
     * @param sender The recipient
     * @param path   The message path
     */
    public void sendRaw(@NotNull CommandSender sender, @NotNull String path) {
        messageService.sendMessage(sender, false, path);
    }

    /**
     * Sends a raw message (no prefix) directly to a CommandSender with placeholders
     *
     * @param sender       The recipient
     * @param path         The message path
     * @param replacements Pairs of key-value replacements
     */
    public void sendRaw(@NotNull CommandSender sender, @NotNull String path, @NotNull Object... replacements) {
        messageService.sendMessage(sender, false, path, replacements);
    }

    /**
     * Checks if a message key exists in the configuration
     */
    public boolean has(@NotNull String path) {
        return messageService.getMessageConfig().has(path);
    }

    public void clearCache() {
        messageService.getMessageConfig().clearCache();
    }

    public int getCacheSize() {
        return messageService.getMessageConfig().getCacheSize();
    }

    public MessageConfig.MessageBuilder builder(String path) {
        return messageService.getMessageConfig().builder(path);
    }
}