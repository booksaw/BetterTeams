package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called right before the tag of a {@link Team} is changed
 */
@Getter
@Setter
public class TeamTagChangeEvent extends TeamEvent {
	private String newTeamTag;

	public TeamTagChangeEvent(@NotNull Team team,
							  @NotNull String newTag) {
		super(team, false);
		this.newTeamTag = newTag;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
}
