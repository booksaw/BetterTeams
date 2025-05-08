package com.booksaw.betterTeams.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.booksaw.betterTeams.Main;

import me.clip.placeholderapi.PlaceholderAPI;

public class StringUtil {
	private StringUtil() {
	}

	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
    public static @NotNull String setPlaceholders(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        if (player == null || !Main.placeholderAPI) {
            return text;
        }

        return PlaceholderAPI.setPlaceholders((OfflinePlayer) player, text);
    }

	
	
    public static @NotNull String setPlaceholders(OfflinePlayer player, String text) {
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
    public static @NotNull String setPlaceholders(String text, Object... replacements) {
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

}
