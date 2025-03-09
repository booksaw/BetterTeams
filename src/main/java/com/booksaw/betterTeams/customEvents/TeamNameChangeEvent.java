package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event which is called right before the renaming of a {@link Team}
 */
@Getter
@Setter
public class TeamNameChangeEvent extends TeamEvent {
	private String newTeamName;
	private final String oldTeamName;
	private final Player player;

	public TeamNameChangeEvent(@NotNull Team team,
							   @NotNull String newTeamName,
							   @Nullable Player player) {
		super(team, false);

		this.oldTeamName = team.getName();
		this.newTeamName = newTeamName;
		this.player = player;
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
