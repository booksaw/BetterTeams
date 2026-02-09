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
 * <p>
 * To modify or cancel the disbanding, use {@link DisbandTeamEvent}.
 *
 * @author svaningelgem
 */
public class PostDisbandTeamEvent extends TeamEvent {

	private static final HandlerList HANDLERS = new HandlerList();
	@Getter
	private final Player player;
	private final Set<UUID> prevAllies;
	private final Set<UUID> prevEnemies;
	@Getter
	private final Set<TeamPlayer> prevMembers;
	private Set<Team> processedAllies = null;
	private Set<Team> processedEnemies = null;

	public PostDisbandTeamEvent(Team team, @Nullable Player player, Set<UUID> previousAllies, Set<UUID> previousEnemies, Set<TeamPlayer> previousMembers) {
		super(team, true);

		this.player = player;
		this.prevAllies = previousAllies;
		this.prevEnemies = previousEnemies;
		this.prevMembers = previousMembers;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	private Set<Team> processTeam(@Nullable Set<UUID> teams) {
		Set<Team> tmp = new HashSet<>();
		for (UUID uuid : teams) {
			Team team = Team.getTeam(uuid);
			if (team != null) {
				tmp.add(team);
			}
		}
		return tmp;
	}

	@NotNull
	public Set<Team> getAllies() {
		if (processedAllies == null) {
			processedAllies = processTeam(prevAllies);
		}

		return processedAllies;
	}

	@NotNull
	public Set<Team> getEnemies() {
		if (processedEnemies == null) {
			processedEnemies = processTeam(prevEnemies);
		}

		return processedEnemies;
	}
}
