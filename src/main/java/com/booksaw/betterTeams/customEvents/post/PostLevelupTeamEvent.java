package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.LevelupTeamEvent;
import com.booksaw.betterTeams.customEvents.TeamEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called after a team's level has been increased.
 * Contains information about the previous and new levels, the cost of the levelup,
 * whether score was used for payment, and the player who initiated the levelup.
 * This event cannot be cancelled since it occurs after the level increase.
 * <p>
 * To modify or cancel the level up, use {@link LevelupTeamEvent}.
 *
 * @author svaningelgem
 */
@Getter
public class PostLevelupTeamEvent extends TeamEvent {

	private static final HandlerList HANDLERS = new HandlerList();
	private final int currentLevel;
	private final int newLevel;
	private final int cost;
	private final boolean score;
	private final Player commandSender;

	public PostLevelupTeamEvent(Team team, int currentLevel, int newLevel, int cost, boolean score, Player commandSender) {
		super(team, true);

		this.currentLevel = currentLevel;
		this.newLevel = newLevel;
		this.cost = cost;
		this.score = score;
		this.commandSender = commandSender;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
