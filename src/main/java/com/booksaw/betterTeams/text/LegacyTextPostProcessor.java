package com.booksaw.betterTeams.text;

import java.util.function.UnaryOperator;

import net.kyori.adventure.text.Component;

final class LegacyTextPostProcessor implements UnaryOperator<Component> {

	LegacyTextPostProcessor() {}

	@Override
	public Component apply(Component c) {
		return c;
	}
}
