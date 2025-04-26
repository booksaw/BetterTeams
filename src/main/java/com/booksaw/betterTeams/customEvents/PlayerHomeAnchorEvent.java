package com.booksaw.betterTeams.customEvents;

import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

import lombok.Getter;

/**
 * Called when a player is about to respawn at their team's home anchor.
 * <p> The player may need to be anchored ({@link TeamPlayer#isAnchored()}) 
 * depending on the conditions set in the 'anchor' section in the config.yml.
 * Additionally, their team must have {@link Team#getTeamHome()} set
 * and have {@link Team#isAnchored()} being true, for this event to be actually called.
 * <p> Useful for knowing about {@link Team#getTeamHome()}, and cancelling it if you
 * don't want the player to respawn there 
 * (otherwise, {@link PlayerRespawnEvent#setRespawnLocation(Location)} with {@link PlayerHomeAnchorEvent#getLocation()} is ran).
 */
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
