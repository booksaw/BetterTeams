package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.PlayerLeaveTeamEvent;
import com.booksaw.betterTeams.customEvents.TeamPlayerEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called after a player has left their team.
 * This event cannot be cancelled since it occurs after the player has left.
 *
 * To modify or cancel the leave action, use {@link PlayerLeaveTeamEvent}.
 *
 * @author svaningelgem
 */
public class PostPlayerLeaveTeamEvent extends TeamPlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public PostPlayerLeaveTeamEvent(Team team, TeamPlayer teamPlayer) {
		super(team, teamPlayer, true);
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

}
