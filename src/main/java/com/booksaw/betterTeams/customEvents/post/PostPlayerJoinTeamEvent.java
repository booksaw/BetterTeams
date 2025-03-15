package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.PlayerJoinTeamEvent;
import com.booksaw.betterTeams.customEvents.TeamPlayerEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called after a player has successfully joined a team.
 * This event cannot be cancelled since it occurs after the player has joined.
 * <p>
 * To modify or cancel the join action, use {@link PlayerJoinTeamEvent}.
 *
 * @author svaningelgem
 */
public class PostPlayerJoinTeamEvent extends TeamPlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public PostPlayerJoinTeamEvent(Team team, TeamPlayer teamPlayer) {
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
