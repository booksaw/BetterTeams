package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.TeamMoneyEvent;
import com.booksaw.betterTeams.customEvents.TeamPlayerEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called when a player deposits money into their {@link Team}'s balance
 */
public final class PostTeamDepositEvent extends TeamPlayerEvent implements TeamMoneyEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final double amount;

    public PostTeamDepositEvent(final Team team, final TeamPlayer teamPlayer, final double amount) {
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
        throw new UnsupportedOperationException("You can't change the amount in a post event.");
    }
}