package com.booksaw.betterTeams.text;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;

final class LegacyTextReplacer implements Consumer<TextReplacementConfig.Builder> {

	private LegacyTextReplacer() {}

	static final LegacyTextReplacer INSTANCE = new LegacyTextReplacer();

	private final static Replacement REPLACEMENT = new Replacement();

	private static final Pattern ALL_PATTERN = Pattern.compile(".*");

	@Override
	public void accept(TextReplacementConfig.Builder builder) {
		builder.match(ALL_PATTERN).replacement(REPLACEMENT);
	}

	private static final class Replacement implements BiFunction<MatchResult, TextComponent.Builder, ComponentLike> {

		@Override
		public ComponentLike apply(MatchResult matchResult, TextComponent.Builder builder) {
			return Legacy.AMPERSAND_SERIALIZER.deserialize(matchResult.group());
		}
	}
}
