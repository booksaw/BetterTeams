package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.TeamEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

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

	public int getCurrentLevel() {
		return currentLevel;
	}

	public int getNewLevel() {
		return newLevel;
	}

	public int getCost() {
		return cost;
	}

	public boolean isScore() {
		return score;
	}

	public Player getCommandSender() {
		return commandSender;
	}

}
