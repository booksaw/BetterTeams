package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.DisbandTeamEvent;
import com.booksaw.betterTeams.customEvents.TeamEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * An event which is called after a team has been disbanded.
 * Contains information about the player who disbanded the team (if applicable),
 * the team's previous allies, and all former team members.
 * This event cannot be cancelled since it occurs after the disbanding.
 *
 * To modify or cancel the disbanding, use {@link DisbandTeamEvent}.
 *
 * @author svaningelgem
 */
public class PostDisbandTeamEvent extends TeamEvent {

	private static final HandlerList HANDLERS = new HandlerList();
	@Getter
	private final Player player;
	private final Set<UUID> prevAllies;
	@Getter
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
}
