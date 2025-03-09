package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.DemotePlayerEvent;
import com.booksaw.betterTeams.customEvents.RankChangePlayerEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called after a player's rank has been lowered within their team.
 * This event tracks both the previous and new rank of the affected player.
 * This event cannot be cancelled since it occurs after the demotion.
 * <p>
 * To modify or cancel the demotion, use {@link DemotePlayerEvent}.
 *
 * @author svaningelgem
 */
public class PostDemotePlayerEvent extends RankChangePlayerEvent {

	public PostDemotePlayerEvent(Team team, TeamPlayer teamPlayer, PlayerRank currentRank, PlayerRank newRank) {
		super(team, teamPlayer, currentRank, newRank, true);
	}

	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

}
