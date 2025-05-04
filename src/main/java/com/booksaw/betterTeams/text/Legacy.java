package com.booksaw.betterTeams.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public final class Legacy {

	private Legacy() {}

    private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)&([0-9A-FK-OR])");
    private static final Pattern STANDARD_HEX_PATTERN = Pattern.compile("(?i)&#([0-9A-F]{6})");
    private static final Pattern BUNGEE_HEX_PATTERN = Pattern.compile("(?i)&x(&[0-9A-F]){6}");
	
    static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
        .hexColors()
        .character('&')
        .hexCharacter('#')
        .build();

	public static String sectionToAmpersand(String s) {
		return s.replace("§", "&");
	}

	public static String bungeeHexToStandardHex(String input) {
		Matcher matcher = BUNGEE_HEX_PATTERN.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hexColor = matcher.group().replace("&x", "").replace("&", "");
            matcher.appendReplacement(buffer, "&#" + hexColor.toUpperCase());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
	}

	public static String bungeeHexToAdventure(String input) {
		Matcher matcher = BUNGEE_HEX_PATTERN.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hexColor = matcher.group().replace("&x", "").replace("&", "");
            matcher.appendReplacement(buffer, "<c:#" + hexColor + ">");
        }
        matcher.appendTail(buffer);
        return buffer.toString();
	}

	public static String standardHexToAdventure(String input) {
		Matcher matcher = STANDARD_HEX_PATTERN.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, "<c:#" + matcher.group(1) + ">");
        }
        matcher.appendTail(buffer);
        return buffer.toString();
	}

	public static String colorToAdventure(ChatColor color) {
		return colorToAdventure(color, false);
	}

    public static String colorToAdventure(ChatColor color, boolean close) {
        if (color == null) {
            return "";
        }

		String colorName = color.getName();
		if (colorName == "underline") {
			colorName = "underlined";
		}

		return "<" + (close && colorName != "reset" ? "/" : "") + colorName + ">";
    }

    public static String colorToAdventure(String input) {
		Matcher matcher = COLOR_PATTERN.matcher(input);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            char code = matcher.group(1).toLowerCase().charAt(0);
            ChatColor color = ChatColor.getByChar(code);
            if (color != null) {
                matcher.appendReplacement(buffer, colorToAdventure(color));
            }
        }
        matcher.appendTail(buffer);
		return buffer.toString();
    }

	public static String toAdventure(String input, boolean bungeeHex, boolean standardHex, boolean chatColor) {
		if (input == null || input.isEmpty()) return "";
		String output = input;
		if (bungeeHex) output = bungeeHexToAdventure(output);
		if (standardHex) output = standardHexToAdventure(output);
		if (chatColor) output = colorToAdventure(output);
		return output;
	}

	public static String parseAllAdventure(String input) {
		return LegacyComponentSerializer.legacySection().serializeOr(Formatter.absolute().process(input), "");
	}

	public static String parseAdventure(String input) {
		return LegacyComponentSerializer.legacySection().serializeOr(Formatter.legacy().process(input), "");
	}

	public static String fromAdventure(Component input) {
		return LegacyComponentSerializer.legacySection().serializeOr(input, "");
	}
}
