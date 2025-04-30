package com.booksaw.betterTeams.message;

import static com.booksaw.betterTeams.message.Formatter.absoluteDeserialize;

import net.kyori.adventure.text.Component;

public abstract class StaticComponentHolderMessage implements ComponentHolderMessage {

	protected final Component message;

	protected StaticComponentHolderMessage(String message) {
		this.message = absoluteDeserialize(message);
	}

	protected StaticComponentHolderMessage(Component message) {
		this.message = message;
	}

	@Override
	public Component getMessage() {
		return this.message;
	}
}
