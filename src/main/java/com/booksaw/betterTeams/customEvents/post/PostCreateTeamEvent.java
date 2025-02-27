package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.CreateTeamEvent;
import com.booksaw.betterTeams.customEvents.TeamEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called immediately after a {@link Team} is successfully created.
 * This event cannot be cancelled since it occurs after the team creation.
 *
 * To modify or cancel the team creation, use {@link CreateTeamEvent}.
 *
 * @author svaningelgem
 */
public class PostCreateTeamEvent extends TeamEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;

    public PostCreateTeamEvent(Team team, Player player) {
        super(team, true);
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

}
