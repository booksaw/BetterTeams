package com.booksaw.betterTeams.exceptions;

import org.bukkit.event.Event;

public class CancelledEventException extends IllegalArgumentException {

	private static final long serialVersionUID = -4344871073409125992L;

	public CancelledEventException(Event event) {
		super("Event of the type " + event.getClass().toString() + " has been cancelled by another plugin");
	}

}
