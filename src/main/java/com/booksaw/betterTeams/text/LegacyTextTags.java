package com.booksaw.betterTeams.text;

import com.google.common.collect.ImmutableSet;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

final class LegacyTextTags {

	static final TagResolver RESET = new TagResolver() {

		private final Set<String> ALIASES = ImmutableSet.of("r", "lr", "lreset", "legacyreset");

		@Override
		public boolean has(final @NotNull String name) {
			return ALIASES.contains(name.toLowerCase());
		}

		@Override
		public @Nullable Tag resolve(final @NotNull String name, final @NotNull ArgumentQueue args, final @NotNull Context ctx) {
			if (!has(name)) return null;

			final NamedTextColor[] color = { NamedTextColor.WHITE }; // default reset color

			if (args.hasNext()) {
				NamedTextColor parsed = NamedTextColor.NAMES.value(args.pop().lowerValue());
				if (parsed != null) {
					color[0] = parsed;
				}
			}
			return Tag.styling(style -> style
					.color(color[0])
					.decoration(TextDecoration.BOLD, false)
					.decoration(TextDecoration.ITALIC, false)
					.decoration(TextDecoration.UNDERLINED, false)
					.decoration(TextDecoration.STRIKETHROUGH, false)
					.decoration(TextDecoration.OBFUSCATED, false));
		}
	};
}
