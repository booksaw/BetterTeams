package com.booksaw.betterTeams.customEvents.post;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called immediately after a the command /teama reload is successfully created.
 * This event cannot be cancelled since it occurs after the team creation.
 *
 * @author svaningelgem
 */
@Getter
public class PostBetterTeamsReloadEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
