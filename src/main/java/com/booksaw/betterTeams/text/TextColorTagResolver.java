package com.booksaw.betterTeams.text;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class TextColorTagResolver implements TagResolver, SerializableResolver.Single {

	private static final Set<String> COLOR_TAG_NAMES = new HashSet<>();

	static {
		COLOR_TAG_NAMES.add("color");
		COLOR_TAG_NAMES.add("c");
	}

    private static final StyleClaim<TextColor> STYLE = StyleClaim.claim("color", Style::color, (color, emitter) -> {
        if (color instanceof NamedTextColor) {
            emitter.tag(NamedTextColor.NAMES.key((NamedTextColor) color));
        }
    });

    private static final Map<String, TextColor> COLOR_ALIASES = new HashMap<>();

    private static boolean isColor(final String name) {
        return COLOR_TAG_NAMES.contains(name);
    }

    private final Set<TextColor> colors = new HashSet<>();

    private TextColorTagResolver(Collection<NamedTextColor> allowedColors) {
        this.colors.addAll(allowedColors);
    }

    @Override
    public @Nullable Tag resolve(final @NotNull String name, final @NotNull ArgumentQueue args, final @NotNull Context ctx) throws ParsingException {
        if (!this.has(name)) {
            return null;
        }

        String colorName;
        if (isColor(name)) {
            colorName = args.popOr("Expected to find a color parameter: <name>|#RRGGBB").lowerValue();
        }
        else {
            colorName = name;
        }

        TextColor color = resolveColor(colorName, ctx);

        if (!this.colors.contains(color)) {
            throw ctx.newException("Color '" + colorName + "' is not allowed.");
        }

        return Tag.styling(color);
    }

    static @NotNull TextColor resolveColor(final @NotNull String colorName, final @NotNull Context ctx) throws ParsingException {
		Objects.requireNonNull(colorName, "colorName");
		Objects.requireNonNull(ctx, "ctx");
        TextColor textColor = COLOR_ALIASES.get(colorName);

        if (textColor != null) {
            return textColor;
        }

        textColor = NamedTextColor.NAMES.value(colorName);

        if (textColor != null) {
            return textColor;
        }

        throw ctx.newException("Unable to parse a color from '" + colorName + "'.");
    }

    @Override
    public boolean has(final @NotNull String name) {
        if (isColor(name)) {
            return true;
        }

        NamedTextColor textColor = NamedTextColor.NAMES.value(name);

        if (textColor != null && this.colors.contains(textColor)) {
            return true;
        }

        return COLOR_ALIASES.containsKey(name);
    }

    @Override
    public @Nullable StyleClaim<?> claimStyle() {
        return STYLE;
    }

    public static TextColorTagResolver of(NamedTextColor... colors) {
        return new TextColorTagResolver(Arrays.asList(colors));
    }

	public static TextColorTagResolver allColorsAllowed() {
        return new TextColorTagResolver(NamedTextColor.NAMES.values());
	}

}