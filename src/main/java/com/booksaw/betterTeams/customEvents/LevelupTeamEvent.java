package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class LevelupTeamEvent extends TeamEvent {

	private static final HandlerList HANDLERS = new HandlerList();
	private final int currentLevel;
	private int newLevel;
	private int cost;
	private boolean score;
	private final Player commandSender;

	public LevelupTeamEvent(Team team, int currentLevel, int newLevel, int cost, boolean score, Player commandSender) {
		super(team, true);

		this.currentLevel = currentLevel;
		this.newLevel = newLevel;
		this.cost = cost;
		this.score = score;
		this.commandSender = commandSender;
	}

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
