package com.booksaw.betterteams.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * This entity is used to represent core information about a team, such as name and description
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamDetails {

	/**
	 * Create a {@link TeamDetails} object,
	 *
	 * @param name        {@link #name}
	 * @param description {@link #description}
	 * @param joinStatus  {@link #joinStatus}
	 * @return the created instance
	 */
	public static TeamDetails of(
			String name,
			String description,
			TeamJoinStatus joinStatus
	) {
		TeamDetails entity = new TeamDetails();
		entity.name = name; // TODO
		entity.description = description; // TODO
		entity.updateTeamJoinStatus(joinStatus);
		return entity;
	}

	/**
	 * The name of the team, this can be changed after the creation of a teams, so
	 * do not store references to it
	 */
	private String name;

	private String description;

	/**
	 * The joining state of the team
	 */
	private TeamJoinStatus joinStatus;

	public void updateTeamJoinStatus(@NonNull TeamJoinStatus joinStatus) {
		this.joinStatus = joinStatus;
	}
}
