package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called when a player deposits money into their {@link com.booksaw.betterTeams.Team}'s balance
 */
@Setter
@Getter
public final class TeamDepositEvent extends TeamPlayerEvent implements TeamMoneyEvent {
	private static final HandlerList HANDLERS = new HandlerList();

	private double amount;

	public TeamDepositEvent(final Team team, final TeamPlayer teamPlayer, final double amount) {
		super(team, teamPlayer, false);

		this.amount = amount;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}