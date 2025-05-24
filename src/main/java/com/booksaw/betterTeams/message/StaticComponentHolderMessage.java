package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.text.Formatter;

import net.kyori.adventure.text.Component;

public abstract class StaticComponentHolderMessage implements ComponentHolderMessage {

	protected final Component message;

	protected StaticComponentHolderMessage(String message) {
		this.message = Formatter.absolute().process(message);
	}

	protected StaticComponentHolderMessage(Component message) {
		this.message = message;
	}

	@Override
	public Component getMessage() {
		return this.message;
	}
}
