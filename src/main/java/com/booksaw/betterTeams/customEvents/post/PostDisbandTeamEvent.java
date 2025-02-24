package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.TeamEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PostDisbandTeamEvent extends TeamEvent {

	private static final HandlerList HANDLERS = new HandlerList();
	private final Player player;
	private final Set<UUID> prevAllies;
	private final Set<TeamPlayer> prevMembers;
	private Set<Team> processedAllies = null;

	public PostDisbandTeamEvent(Team team, @Nullable Player player, Set<UUID> previousAllies, Set<TeamPlayer> previousMembers) {
		super(team, true);
		this.player = player;
		this.prevAllies = previousAllies;
		this.prevMembers = previousMembers;
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

	@NotNull
	public Set<Team> getAllies() {
		if (processedAllies == null) {
			processedAllies = new HashSet<>();
			for (UUID uuid : prevAllies) {
				Team team = Team.getTeam(uuid);
				if (team != null) {
					processedAllies.add(team);
				}
			}
		}

		return processedAllies;
	}

	@NotNull
	public Set<TeamPlayer> getMembers() {
		return prevMembers;
	}
}
