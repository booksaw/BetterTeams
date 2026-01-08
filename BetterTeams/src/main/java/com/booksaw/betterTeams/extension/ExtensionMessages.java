package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.ConfigManager;
import com.booksaw.betterTeams.message.StaticMessage;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtensionMessages {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private final BetterTeamsExtension extension;
    private final Map<String, String> cache;
    @Getter
    private ConfigManager configManager;
    @Getter
    private String fileName;

    public ExtensionMessages(@NotNull BetterTeamsExtension extension, @NotNull String fileName) {
        this.extension = extension;
        this.cache = new HashMap<>();
        this.fileName = fileName;
        reload();
    }

    public void reload() {
        reload(this.fileName);
    }

    /**
     * Reloads messages from a different file
     * @param fileName The new file name (without .yml extension)
     */
    public void reload(@NotNull String fileName) {
        if (fileName.isEmpty()) throw new IllegalArgumentException("File name cannot be empty");
        this.fileName = fileName;
        clearCache();
        this.configManager = new ConfigManager(fileName, false, extension);
        extension.getLogger().info("Messages loaded from " + fileName + ".yml");
    }


    /**
     * Gets a raw string from config with color codes translated
     *
     * @param path The path in messages file
     * @return The formatted message, or an error string if not found
     */
    @NotNull
    public String get(@NotNull String path) {
        String cached = cache.get(path);
        if (cached != null) {
            return cached;
        }

        FileConfiguration config = configManager.getConfig();
        String val = config.getString(path);

        if (val == null) {
            String error = ChatColor.RED + "[Missing: " + path + "]";
            cache.put(path, error);
            extension.getLogger().warning("Missing message key: " + path);
            return error;
        }

        String colored = translateColors(val);
        cache.put(path, colored);
        return colored;
    }

    /**
     * Gets a message with placeholders replaced
     */
    @NotNull
    public String get(@NotNull String path, @NotNull Object... replacements) {
        String message = get(path);
        if (replacements.length >= 2) {
            for (int i = 0; i < replacements.length - 1; i += 2) {
                String key = String.valueOf(replacements[i]);
                String value = String.valueOf(replacements[i + 1]);
                message = message.replace("{" + key + "}", value);
            }
        }
        return message;
    }

    private String translateColors(@NotNull String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    @NotNull
    public String getPrefix() {
        return get("prefix");
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
     * @param path         The message path
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
     * @param success      Whether the command was successful
     * @param path         The message path
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
        toStatic(path).sendMessage(sender);
    }

    /**
     * Sends a message directly to a CommandSender with prefix and placeholders
     *
     * @param sender       The recipient
     * @param path         The message path
     * @param replacements Pairs of key-value replacements
     */
    public void send(@NotNull CommandSender sender, @NotNull String path, @NotNull Object... replacements) {
        toStatic(path, replacements).sendMessage(sender);
    }

    /**
     * Sends a message directly to multiple CommandSenders with prefix
     *
     * @param recipients The recipients
     * @param path       The message path
     */
    public void send(@NotNull Collection<? extends CommandSender> recipients, @NotNull String path) {
        toStatic(path).sendMessage(recipients);
    }

    /**
     * Sends a message directly to multiple CommandSenders with prefix and placeholders
     *
     * @param recipients   The recipients
     * @param path         The message path
     * @param replacements Pairs of key-value replacements
     */
    public void send(@NotNull Collection<? extends CommandSender> recipients, @NotNull String path, @NotNull Object... replacements) {
        toStatic(path, replacements).sendMessage(recipients);
    }

    /**
     * Sends a raw message (no prefix) directly to a CommandSender
     *
     * @param sender The recipient
     * @param path   The message path
     */
    public void sendRaw(@NotNull CommandSender sender, @NotNull String path) {
        toStaticRaw(path).sendMessage(sender);
    }

    /**
     * Sends a raw message (no prefix) directly to a CommandSender with placeholders
     *
     * @param sender       The recipient
     * @param path         The message path
     * @param replacements Pairs of key-value replacements
     */
    public void sendRaw(@NotNull CommandSender sender, @NotNull String path, @NotNull Object... replacements) {
        toStaticRaw(path, replacements).sendMessage(sender);
    }

    /**
     * Checks if a message key exists in the configuration
     */
    public boolean has(@NotNull String path) {
        return configManager.getConfig().contains(path);
    }

    @NotNull
    public MessageBuilder builder(@NotNull String path) {
        return new MessageBuilder(this, path);
    }

    /**
     * Clears the message cache.
     */
    public void clearCache() {
        cache.clear();
    }

    public int getCacheSize() {
        return cache.size();
    }


    public static class MessageBuilder {
        private final ExtensionMessages messages;
        private String text;

        public MessageBuilder(@NotNull ExtensionMessages messages, @NotNull String path) {
            this.messages = messages;
            this.text = messages.get(path);
        }

        /**
         * Replaces a placeholder with a value
         * Placeholders are in the format {key}
         *
         * @param key   The placeholder key (without braces)
         * @param value The replacement value
         * @return This builder for chaining
         */
        @NotNull
        public MessageBuilder with(@NotNull String key, @Nullable Object value) {
            this.text = this.text.replace("{" + key + "}", String.valueOf(value));
            return this;
        }

        /**
         * Replaces multiple placeholders from a map
         *
         * @param placeholders Map of key-value pairs
         * @return This builder for chaining
         */
        @NotNull
        public MessageBuilder withAll(@NotNull Map<String, Object> placeholders) {
            for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
                with(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @NotNull
        public String build() {
            return text;
        }

        @NotNull
        public String buildWithPrefix() {
            return messages.getPrefix() + text;
        }

        @NotNull
        public StaticMessage toStatic() {
            return new StaticMessage(buildWithPrefix());
        }

        @NotNull
        public StaticMessage toStaticRaw() {
            return new StaticMessage(build());
        }

        @NotNull
        public CommandResponse toResponse(boolean success) {
            return new CommandResponse(success, toStatic());
        }

        public void send(@NotNull CommandSender sender) {
            toStatic().sendMessage(sender);
        }

        public void sendRaw(@NotNull CommandSender sender) {
            toStaticRaw().sendMessage(sender);
        }
    }
}