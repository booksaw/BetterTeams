package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called right before the creation of a {@link Team}
 */
@Getter
public class CreateTeamEvent extends TeamEvent {

	private static final HandlerList HANDLERS = new HandlerList();
	private final Player player;

	public CreateTeamEvent(Team team, Player player) {
		super(team, false);
		this.player = player;
	}

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
