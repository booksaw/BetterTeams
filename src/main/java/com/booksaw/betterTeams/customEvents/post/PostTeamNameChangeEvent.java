package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.TeamEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called right before the renaming of a {@link Team}
 */
public class PostTeamNameChangeEvent extends TeamEvent {
	private final String oldName;
	private final String newName;

	public PostTeamNameChangeEvent(@NotNull Team team, @NotNull String oldName, @NotNull String newName) {
		super(team);
		this.oldName = oldName;
		this.newName = newName;
	}

	public String getOldTeamName() {
		return oldName;
	}

	public String getNewTeamName() {
		return newName;
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
