package com.booksaw.betterTeams.text;

import java.util.function.UnaryOperator;

final class LegacyTextPreProcessor implements UnaryOperator<String> {

	LegacyTextPreProcessor() {}

	@Override
	public String apply(String input) {
		return LegacyTextUtils.toAdventure(input);
	}
}
