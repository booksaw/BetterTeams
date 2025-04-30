package com.booksaw.betterTeams.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.booksaw.betterTeams.Main;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class Formatter {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{1,8})");
    private static final Pattern LEGACY_TAG_PATTERN = Pattern.compile("[§&]([0-9a-fk-orxA-FK-ORX])");
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");

    private static final MiniMessage MM = MiniMessage.miniMessage();
	private static final MiniMessage PLAYER_MM = MiniMessage.builder()
			.tags(TagResolver.resolver(
					StandardTags.color(),
					StandardTags.decorations(),
					StandardTags.gradient(),
					StandardTags.rainbow()))
			.build();

    /**
     * Translates RGB color codes (&#C -> &#CCCCCCCC) into MiniMessage color tags
     *
     * @param message The message to translate.
     * @return The translated message with RGB colors applied.
     */
	@NotNull
    public static String translateRgbToMiniMessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        Matcher matcher = HEX_PATTERN.matcher(message);

        StringBuffer translatedMessage = new StringBuffer();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            matcher.appendReplacement(translatedMessage, "<color:#" + hexColor + ">");
        }

        matcher.appendTail(translatedMessage);
        return translatedMessage.toString();
    }

	@NotNull
    public static String legacyTagToMiniMessage(@Nullable ChatColor color) {
        if (color == null) {
            return "";
        }

		String colorName = color.getName().toLowerCase();
		if (colorName == "underline") {
			colorName = "underlined";
		}

		return "<" + colorName + ">";
    }

	@NotNull
    public static String legacyTagToMiniMessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        Matcher legacyMatcher = LEGACY_TAG_PATTERN.matcher(message);
        StringBuffer convertedMessage = new StringBuffer();

        while (legacyMatcher.find()) {
            char code = legacyMatcher.group(1).toLowerCase().charAt(0); // Get the character after § or &
            ChatColor chatColor = ChatColor.getByChar(code); // Get the ChatColor associated with the code

            if (chatColor != null) {
                String miniMessageTag = legacyTagToMiniMessage(chatColor); // Get the MiniMessage tag
                legacyMatcher.appendReplacement(convertedMessage, miniMessageTag);
            }
        }
		
        legacyMatcher.appendTail(convertedMessage);

        return convertedMessage.toString();
    }

	@NotNull
    public static String legacyHexToMiniMessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
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
	@NotNull
    public static String legacyToMiniMessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        message = legacyHexToMiniMessage(message);
        message = legacyTagToMiniMessage(message);

        return message;
    }

	@NotNull
    public static String legacyTranslate(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        return translateRgbToMiniMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

	@NotNull
    public static String absoluteTranslate(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        return legacyToMiniMessage(legacyTranslate(message));
    }

    /**
     * Converts a message to a Component using this class' MiniMessage instance.
     *
     * @param message The message to format.
     * @return The formatted Component.
     */
	@NotNull
    public static Component deserializeWithMiniMessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        return MM.deserialize(message);
    }
	
    /**
     * Converts a message to a Component using this class' player-exclusive MiniMessage.
     *
     * @param message The message to format.
     * @return The formatted Component.
     */
	@NotNull
    public static Component deserializeWithPlayerMiniMessage(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        return PLAYER_MM.deserialize(message);
    }

	@NotNull
    public static Component absoluteDeserialize(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        return MM.deserialize(absoluteTranslate(message));
    }

	@NotNull
	public static Component absolutePlayerDeserialize(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

		return PLAYER_MM.deserialize(absoluteTranslate(message));
	}

	@NotNull
	public static Component combineComponents(Component@NotNull... components) {
		if (components == null || components.length == 0) {
			return Component.empty();
		} else if (components.length == 1) {
			return components[0] != null ? components[0] : Component.empty();
		}
		// From here on, Adventure Text handles possibly null Components
		return Component.join(JoinConfiguration.noSeparators(), components);
	}

	@NotNull
	public static Component prefixComponents(Component prefix, @NotNull Component@NotNull... components) {
		if (prefix == null || prefix.equals(Component.empty())) {
			return combineComponents(components);
		}
		return Component.join(JoinConfiguration.builder().prefix(prefix).build(), components);
	}

	@NotNull
    public static String legacySerialize(@Nullable Component component) {
        if (component == null || component.equals(Component.empty())) {
            return "";
        }

        return LegacyComponentSerializer.legacySection().serialize(component);
    }

	@NotNull
    public static String legacySerialize(@Nullable String string) {
        return legacySerialize(string, true);
    }

    @NotNull
    public static String legacySerialize(@Nullable String string, boolean translate) {
        if (string == null || string.isEmpty()) {
            return "";
        }

        if (translate) {
            string = absoluteTranslate(string);
        }

        return legacySerialize(deserializeWithMiniMessage(string));
    }

    @NotNull
    public static String setPlaceholders(@Nullable String text, @Nullable Player player) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        if (player == null || !Main.placeholderAPI) {
            return text;
        }

        return PlaceholderAPI.setPlaceholders((OfflinePlayer) player, text);
    }

    @NotNull
    public static String setPlaceholders(@Nullable String text, @Nullable OfflinePlayer player) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        if (player == null || !Main.placeholderAPI) {
            return text;
        }

        return PlaceholderAPI.setPlaceholders(player, text);
    }

    /**
     * Replaces indexed placeholders in the provided text with the corresponding replacement values.
     * <p>
     * Each placeholder in the text should follow the format {n}, where {@code n} is the zero-based index
     * of the replacement value to insert. For example, "Hello, {0}!" with {@code "world"} as the first
     * replacement will result in {@code "Hello, world!"}.
     * <p>
     * If the {@code text} is {@code null} or empty, an empty string is returned. If no replacements are provided,
     * the original text is returned unmodified.
     * <p>
     * {@code null} values in the replacement array are treated as empty strings.
     *
     * @param text the text containing indexed placeholders like {0}, {1}, etc.
     * @param replacements the values to insert into the placeholders
     * @return a new string with all placeholders replaced by their corresponding values
     */
    @NotNull
    public static String setPlaceholders(@Nullable String text, @Nullable Object... replacements) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        if (replacements == null || replacements.length == 0) {
            return text;
        }

        StringBuilder formatted = new StringBuilder(text);
        for (int i = 0; i < replacements.length; i++) {
            String placeholder = "{" + i + "}";
            String replacement = replacements[i] != null ? replacements[i].toString() : "";

            int index;
            while ((index = formatted.indexOf(placeholder)) != -1) {
                formatted.replace(index, index + placeholder.length(), replacement);
            }
        }
        return formatted.toString();
    }

	@NotNull
	public static Component setPlaceholders(Component base, Component replacement, String... placeholders) {
		if (base == null || base.equals(Component.empty())) return Component.empty();

		if (placeholders == null || placeholders.length == 0 || replacement == null || replacement.equals(Component.empty())) return base;

		TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
		boolean hasReplacements = false;

		for (String placeholder : placeholders) {
			if (placeholder == null || placeholder.isEmpty()) continue;

			builder.matchLiteral(placeholder).replacement(replacement);
			hasReplacements = true;
		}

		if (!hasReplacements) return base;

		return base.replaceText(builder.build());
	}
}
