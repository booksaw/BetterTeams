package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called when a player withdraws money from their {@link com.booksaw.betterTeams.Team}'s balance
 */
public final class TeamWithdrawEvent extends TeamPlayerEvent implements TeamMoneyEvent {
	private static final HandlerList HANDLERS = new HandlerList();

	private double amount;

	public TeamWithdrawEvent(final Team team, final TeamPlayer teamPlayer, final double amount) {
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

	@Override
	public double getAmount() {
		return this.amount;
	}

	@Override
	public void setAmount(final double amount) {
		this.amount = amount;
	}
}