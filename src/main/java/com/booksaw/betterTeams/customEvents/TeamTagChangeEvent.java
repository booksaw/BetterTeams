package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called right before the tag of a {@link Team} is changed
 */
public class TeamTagChangeEvent extends TeamEvent {
	private String newTag;

	public TeamTagChangeEvent(@NotNull Team team,
							  @NotNull String newTag) {
		super(team);
		this.newTag = newTag;
	}

	public String getNewTeamTag() {
		return newTag;
	}

	public void setNewTeamTag(String newTagToSet) {
		this.newTag = newTagToSet;
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
