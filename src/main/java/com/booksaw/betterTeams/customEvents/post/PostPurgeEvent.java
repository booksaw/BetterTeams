package com.booksaw.betterTeams.customEvents.post;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This command is called just before a purge occurs
 *
 * @author booksaw
 */
public class PostPurgeEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	private boolean cancelled = false;

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public PostPurgeEvent() {
		super(true);
	}
	
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;

	}

}
