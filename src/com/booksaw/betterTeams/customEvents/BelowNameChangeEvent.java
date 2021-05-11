package com.booksaw.betterTeams.customEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Used to track the details of a below name change event
 * 
 * @author booksaw
 *
 */
public class BelowNameChangeEvent extends Event {

	private final Player player;
	private final ChangeType type;

	public BelowNameChangeEvent(Player player, ChangeType type) {
		this.player = player;
		this.type = type;
	}

	public Player getPlayer() {
		return player;
	}

	public ChangeType getType() {
		return type;
	}

	public enum ChangeType {
		ADD, REMOVE
	}

	private static final HandlerList HANDLERS = new HandlerList();

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
