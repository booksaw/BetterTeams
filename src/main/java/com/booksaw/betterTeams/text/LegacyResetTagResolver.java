package com.booksaw.betterTeams.text;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public enum LegacyResetTagResolver implements TagResolver {
    INSTANCE;

    @Override
    public boolean has(final String name) {
        return ALIASES.contains(name.toLowerCase());
    }

	private static final Set<String> ALIASES = new ImmutableSet.Builder<String>()
			.add("r", "lr", "lreset", "legacyreset").build();

    @Override
    public Tag resolve(final String name, final ArgumentQueue args, final Context ctx) {
		if (!has(name)) return null;

        return Tag.styling(style -> style
            .color(NamedTextColor.WHITE)
            .decoration(TextDecoration.BOLD, false)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.UNDERLINED, false)
            .decoration(TextDecoration.STRIKETHROUGH, false)
            .decoration(TextDecoration.OBFUSCATED, false)
        );
    }
}
