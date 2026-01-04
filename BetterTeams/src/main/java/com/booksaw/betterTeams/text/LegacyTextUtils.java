package com.booksaw.betterTeams.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LegacyTextUtils {

	private LegacyTextUtils() {
	}

	private static final Pattern MOJANG_COLOR_PATTERN = Pattern.compile("(?i)&([0-9A-FK-OR])");
	private static final Pattern STANDARD_HEX_PATTERN = Pattern.compile("(?i)&#([0-9A-F]{6})");
	private static final Pattern BUNGEE_HEX_PATTERN = Pattern.compile("(?i)&x(&[0-9A-F]){6}");

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
			matcher.appendReplacement(buffer, "<lr><c:#" + hexColor + ">");
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
		return colorToAdventure(color, false, new ArrayList<>());
	}

	public static String colorToAdventure(ChatColor color, boolean close) {
		return colorToAdventure(color, close, new ArrayList<>());
	}

	private static String colorToAdventure(ChatColor color, boolean close, List<String> closeList) {
		if (color == null) {
			return "";
		}
		String colorName = color.getName();
		if (colorName == "underline") {
			colorName = "underlined";
		}

		if (colorName == "reset") {
			return "<lr>";
		} else {
			if (!close) {
				closeList.add("</" + colorName + ">");
			}
			return "<" + (close ? "/" : "") + colorName + ">";
		}
	}

	public static String colorToAdventure(String input) {
		Matcher matcher = MOJANG_COLOR_PATTERN.matcher(input);
		StringBuffer buffer = new StringBuffer();

		// All elements to be closed at the next color code
		List<String> closeList = new ArrayList<>();

		while (matcher.find()) {
			char code = matcher.group(1).toLowerCase().charAt(0);
			ChatColor color = ChatColor.getByChar(code);

			if (color != null) {
				String replacement = "";
				if (Character.digit(code, 16) != -1) {
					// if char is a color, must close all previous tags before opening the new color
					// otherwise bold etc. will persist, reverting as inside tag must be closed first
					Collections.reverse(closeList);
					replacement += String.join("", closeList);
					closeList.clear();
				}
				replacement += colorToAdventure(color, false, closeList);
				matcher.appendReplacement(buffer, replacement);
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	public static String toAdventure(String input) {
		return toAdventure(input, true, true, true, true);
	}

	public static String toAdventure(String input, boolean sectionToAmpersand, boolean bungeeHex, boolean standardHex, boolean chatColor) {
		if (input == null || input.isEmpty()) return "";
		String output = input;
		if (sectionToAmpersand) output = sectionToAmpersand(output);
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

	public static String serialize(Component input) {
		return LegacyComponentSerializer.legacySection().serializeOr(input, "");
	}
}
