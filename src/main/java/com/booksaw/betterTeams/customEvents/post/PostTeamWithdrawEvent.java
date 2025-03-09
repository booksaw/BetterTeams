package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.TeamPlayerEvent;
import com.booksaw.betterTeams.customEvents.TeamWithdrawEvent;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called after a player has withdrawn money from their {@link Team}'s balance.
 * Contains information about the amount withdrawn and the player who made the withdrawal.
 * This event cannot be cancelled since it occurs after the withdrawal.
 * <p>
 * To modify or cancel the withdrawal, use {@link TeamWithdrawEvent}.
 *
 * @author svaningelgem
 */
@Getter
public final class PostTeamWithdrawEvent extends TeamPlayerEvent implements PostTeamMoneyEvent {
	private static final HandlerList HANDLERS = new HandlerList();

	private final double amount;

	public PostTeamWithdrawEvent(final Team team, final TeamPlayer teamPlayer, final double amount) {
		super(team, teamPlayer, true);

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