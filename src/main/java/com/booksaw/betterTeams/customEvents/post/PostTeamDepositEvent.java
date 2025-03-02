package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.TeamDepositEvent;
import com.booksaw.betterTeams.customEvents.TeamPlayerEvent;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called after a player has deposited money into their {@link Team}'s balance.
 * Contains information about the amount deposited and the player who made the deposit.
 * This event cannot be cancelled since it occurs after the deposit.
 * <p>
 * To modify or cancel the deposit, use {@link TeamDepositEvent}.
 *
 * @author svaningelgem
 */
@Getter
public final class PostTeamDepositEvent extends TeamPlayerEvent implements PostTeamMoneyEvent {
	private static final HandlerList HANDLERS = new HandlerList();

	private final double amount;

	public PostTeamDepositEvent(final Team team, final TeamPlayer teamPlayer, final double amount) {
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
