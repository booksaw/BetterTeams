package com.booksaw.betterTeams.util;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;

public class ComponentUtil {

	public static @NotNull Component combineComponents(Component... components) {
		if (components == null || components.length == 0) {
			return Component.empty();
		} else if (components.length == 1) {
			return components[0] != null ? components[0] : Component.empty();
		}
		// From here on, Adventure Text handles possibly null Components
		return Component.join(JoinConfiguration.noSeparators(), components);
	}

	public static @NotNull Component prefixComponents(Component prefix, Component... components) {
		if (prefix == null || prefix.equals(Component.empty())) {
			return combineComponents(components);
		}
		return Component.join(JoinConfiguration.builder().prefix(prefix).build(), components);
	}

	public static @NotNull Component setPlaceholders(Component base, Component replacement, String... placeholders) {
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
