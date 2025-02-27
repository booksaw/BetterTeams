package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.TeamEvent;
import com.booksaw.betterTeams.customEvents.TeamNameChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event which is called after a {@link Team}'s name has been changed.
 * Contains information about both the old and new team names.
 * This event cannot be cancelled since it occurs after the name change.
 *
 * To modify or cancel the name change, use {@link TeamNameChangeEvent}.
 *
 * @author svaningelgem
 */
public class PostTeamNameChangeEvent extends TeamEvent {
	private final String oldName;
	private final String newName;
	private final Player player;

	public PostTeamNameChangeEvent(@NotNull Team team, @NotNull String oldName, @NotNull String newName,
	                               @Nullable Player player) {
		super(team);
		this.oldName = oldName;
		this.newName = newName;
		this.player = player;
	}

	public String getOldTeamName() {
		return oldName;
	}

	public String getNewTeamName() {
		return newName;
	}

	public Player getPlayer() {
		return player;
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
