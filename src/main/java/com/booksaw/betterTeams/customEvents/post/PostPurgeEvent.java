package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.customEvents.PrePurgeEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is called after a team purge has been completed.
 * This event cannot be cancelled since it occurs after the purge.
 *
 * To modify or cancel the purge, use {@link PrePurgeEvent}.
 *
 * @author svaningelgem
 */
public class PostPurgeEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

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
}
