package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event which is called right before the renaming of a {@link Team}
 */
public class TeamNameChangeEvent extends TeamEvent {

	private String newName;
	private final Player player;

	public TeamNameChangeEvent(@NotNull Team team,
							   @NotNull String newName,
							   @Nullable Player player) {
		super(team);
		this.newName = newName;
		this.player = player;
	}

	public String getNewTeamName() {
		return newName;
	}

	public Player getPlayer() {
		return player;
	}

	public void setNewTeamName(String newNameToSet) {
		this.newName = newNameToSet;
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
