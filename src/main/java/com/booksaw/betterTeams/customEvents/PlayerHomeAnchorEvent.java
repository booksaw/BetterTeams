package com.booksaw.betterTeams.customEvents;

import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

import lombok.Getter;

public class PlayerHomeAnchorEvent extends TeamPlayerEvent {

	@Getter
	private @NotNull Location location;

    public PlayerHomeAnchorEvent(@NotNull Team team, @NotNull TeamPlayer teamPlayer, @NotNull Location location){
		super(team, teamPlayer, false);
		this.location = location;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
