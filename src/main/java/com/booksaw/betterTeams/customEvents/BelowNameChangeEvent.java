package com.booksaw.betterTeams.customEvents;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Used to track the details of a below name change event
 * Unlike most other events in BetterTeams, this one cannot be cancelled!
 *
 * @author booksaw
 */
@Getter
public class BelowNameChangeEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	private final Player player;
	private final ChangeType type;

	public BelowNameChangeEvent(Player player, ChangeType type) {
		super(true);

		this.player = player;
		this.type = type;
	}

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public enum ChangeType {
		ADD, REMOVE
	}
}
