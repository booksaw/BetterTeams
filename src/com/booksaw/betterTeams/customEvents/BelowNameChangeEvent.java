package com.booksaw.betterTeams.customEvents;

import org.bukkit.entity.Player;

/**
 * Used to track the details of a below name change event
 * 
 * @author booksaw
 *
 */
public class BelowNameChangeEvent {

	private Player player;
	private ChangeType type;

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
		ADD, REMOVE;
	}

}
