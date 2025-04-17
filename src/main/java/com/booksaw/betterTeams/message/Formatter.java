package com.booksaw.betterTeams.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.booksaw.betterTeams.Main;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class Formatter {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern LEGACY_TAG_PATTERN = Pattern.compile("[§&]([0-9a-fk-orxA-FK-ORX])");
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");

    private static final MiniMessage MM = MiniMessage.miniMessage();

    /**
     * Translates RGB color codes (&#RRGGBB) into Minecraft color codes.
     *
     * @param message The message to translate.
     * @return The translated message with RGB colors applied.
     */
    public static @NotNull String translateRGBColors(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
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

    public static @NotNull String legacyTagToMinimessage(@Nullable ChatColor color) {
        if (color == null) {
            return "";
        }
        return "<" + color.getName() + ">";
    }

    public static @NotNull String legacyTagToMinimessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        Matcher legacyMatcher = LEGACY_TAG_PATTERN.matcher(message);
        StringBuffer convertedMessage = new StringBuffer();

        while (legacyMatcher.find()) {
            char code = legacyMatcher.group(1).toLowerCase().charAt(0); // Get the character after § or &
            ChatColor chatColor = ChatColor.getByChar(code); // Get the ChatColor associated with the code

            if (chatColor != null) {
                String miniMessageTag = legacyTagToMinimessage(chatColor); // Get the MiniMessage tag
                legacyMatcher.appendReplacement(convertedMessage, miniMessageTag);
            }
        }
        legacyMatcher.appendTail(convertedMessage);

        return convertedMessage.toString();
    }

    public static @NotNull String legacyHexToMinimessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        // Convert Minecraft's hex color format (§x§R§R§G§G§B§B) to MiniMessage format
        // (<color:#RRGGBB>)
        Matcher hexMatcher = LEGACY_HEX_PATTERN.matcher(message);
        StringBuffer convertedMessage = new StringBuffer();

        while (hexMatcher.find()) {
            // Extract the hex color from the matched format
            String hexColor = hexMatcher.group().replace("§x", "").replace("§", "");
            hexMatcher.appendReplacement(convertedMessage, "<color:#" + hexColor + ">");
        }
        hexMatcher.appendTail(convertedMessage);

        // Update the message with the converted hex colors
        return convertedMessage.toString();
    }

    /**
     * Transforms legacy formatting codes (§ or &) and Minecraft's hex color format
     * (§x§R§R§G§G§B§B)
     * into MiniMessage-compatible tags.
     *
     * @param message The message to transform.
     * @return The transformed message with MiniMessage-compatible tags.
     */
    public static @NotNull String legacyToMinimessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        message = legacyHexToMinimessage(message);
        message = legacyTagToMinimessage(message);

        return message;
    }

    public static @NotNull String legacyTranslate(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        return translateRGBColors(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static @NotNull String absoluteTranslate(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        return legacyToMinimessage(legacyTranslate(message));
    }

    /**
     * Converts a message to a Component using this class' MiniMessage instance.
     *
     * @param message The message to format.
     * @return The formatted Component.
     */
    public static @NotNull Component deserializeWithMiniMessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return MM.deserialize(message);
    }

    public static @NotNull Component absoluteMinimessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return MM.deserialize(absoluteTranslate(message));
    }

    public static @NotNull String legacySerialize(@Nullable Component component) {
        if (component == null || component.equals(Component.empty())) {
            return "";
        }
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static @NotNull String legacySerialize(@Nullable String string) {
        return legacySerialize(string, true);
    }

    public static @NotNull String legacySerialize(@Nullable String string, boolean translate) {
        if (string == null || string.isEmpty()) {
            return "";
        }

        if (translate) {
            string = absoluteTranslate(string);
        }

        return legacySerialize(deserializeWithMiniMessage(string));
    }

    public static @NotNull String setPlaceholders(@Nullable String text, @Nullable Player player) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (player == null || !Main.placeholderAPI) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public static @Nullable String setPlaceholders(@Nullable String text, Object... replacement) {
        if (text == null || text.isEmpty())
            return "";
        if (replacement == null || replacement.length == 0)
            return text;

        String formatted = text;
        for (int i = 0; i < replacement.length; i++) {
            formatted = formatted.replace("{" + i + "}", replacement[i].toString());
        }
        return formatted;
    }
}
