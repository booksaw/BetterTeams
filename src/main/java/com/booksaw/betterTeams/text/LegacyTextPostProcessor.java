package com.booksaw.betterTeams.text;

import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;

final class LegacyTextPostProcessor implements UnaryOperator<Component> {

	LegacyTextPostProcessor() {}

	@Override
	public Component apply(Component c) {
		Component parent = c.replaceText(LegacyTextReplacer.INSTANCE);
		// Recursion ends when the parent component has no children. vvv
		return parent.children(parent.children().stream().map(this::apply).collect(Collectors.toList()));
	}
}
