package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.RelationType;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.RelationChangeTeamEvent;
import com.booksaw.betterTeams.customEvents.TeamEvent;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event which is called right after the changing of a relation with a {@link Team} (ie: ally/neutral)
 * <p>
 * To modify or cancel the color change, use {@link RelationChangeTeamEvent}.
 */
@SuppressWarnings("unused")
@Getter
public class PostRelationChangeTeamEvent extends TeamEvent {

	final private @NotNull Team otherTeam;
	final private @NotNull RelationType previousRelation;
	final private @NotNull RelationType newRelation;

	public PostRelationChangeTeamEvent(@NotNull Team team,
									   @NotNull Team otherTeam,
									   @NotNull RelationType previousRelation,
									   @NotNull RelationType newRelation
	) {
		super(team, true);

		this.otherTeam = otherTeam;
		this.previousRelation = previousRelation;
		this.newRelation = newRelation;
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
