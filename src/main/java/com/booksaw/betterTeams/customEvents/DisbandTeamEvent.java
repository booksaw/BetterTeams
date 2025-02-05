package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisbandTeamEvent extends TeamEvent {

	private static final HandlerList HANDLERS = new HandlerList();
	private final Player player;

	public DisbandTeamEvent(Team team, @Nullable Player player) {
		super(team);
		this.player = player;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	@Nullable
	public Player getPlayer() {
		return player;
	}

}
