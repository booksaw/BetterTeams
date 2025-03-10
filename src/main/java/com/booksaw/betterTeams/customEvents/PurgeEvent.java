package com.booksaw.betterTeams.customEvents;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This command is called just before a purge occurs
 *
 * @author booksaw
 */
@Getter
@Setter
public class PurgeEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	private boolean cancelled = false;

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public PurgeEvent() {
		super(true);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
