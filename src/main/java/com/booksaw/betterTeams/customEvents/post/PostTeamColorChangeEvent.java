package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.TeamEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called after a {@link Team}'s color has been changed.
 * Contains information about both the old and new team colors.
 * This event cannot be cancelled since it occurs after the color change.
 *
 * To modify or cancel the color change, use {@link TeamColorChangeEvent}.
 *
 * @author svaningelgem
 */
public class PostTeamColorChangeEvent extends TeamEvent {
    private final ChatColor oldColor;
    private final ChatColor newColor;

    public PostTeamColorChangeEvent(@NotNull Team team,
                                    @NotNull ChatColor oldColor,
                                    @NotNull ChatColor newColor) {
        super(team, true);
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    public ChatColor getOldTeamColor() {
        return oldColor;
    }

    public ChatColor getNewTeamColor() {
        return newColor;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
