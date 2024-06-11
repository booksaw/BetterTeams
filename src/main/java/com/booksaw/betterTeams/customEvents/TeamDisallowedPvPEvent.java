package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event called when determining whether to allow or disallow a pvp interaction between members of the same {@link com.booksaw.betterTeams.Team}
 */
public final class TeamDisallowedPvPEvent extends TeamEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player source;
    private final Team damagerTeam;

    private boolean isProtected; // Is an ally or on the same team

    public TeamDisallowedPvPEvent(final Team victimTeam, final Player source, final Team damagerTeam, final boolean isProtected) {
        super(victimTeam);

        this.source = source;
        this.damagerTeam = damagerTeam;
        this.isProtected = isProtected;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getSource() {
        return this.source;
    }

    public Team getDamagerTeam() {
        return this.damagerTeam;
    }

    public boolean isProtected() {
        return this.isProtected;
    }
}