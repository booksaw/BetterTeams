package com.booksaw.betterTeams.customEvents;

public interface TeamMoneyEvent {
    double getAmount();

    void setAmount(final double amount);

    boolean isCancelled();

    void setCancelled(final boolean value);
}