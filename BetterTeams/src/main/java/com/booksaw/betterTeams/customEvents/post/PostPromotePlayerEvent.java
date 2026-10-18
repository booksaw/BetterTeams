package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.PromotePlayerEvent;
import com.booksaw.betterTeams.customEvents.RankChangePlayerEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called after a player's rank has been increased within their team.
 * This event tracks both the previous and new rank of the affected player.
 * This event cannot be cancelled since it occurs after the promotion.
 * <p>
 * To modify or cancel the promotion, use {@link PromotePlayerEvent}.
 *
 * @author svaningelgem
 */
public class PostPromotePlayerEvent extends RankChangePlayerEvent {

	public PostPromotePlayerEvent(Team team, TeamPlayer teamPlayer, PlayerRank currentRank, PlayerRank newRank) {
		super(team, teamPlayer, currentRank, newRank);
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
