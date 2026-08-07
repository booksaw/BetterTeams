package com.booksaw.betterteams.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * This Class is the domain representation of a Team
 * <p>
 * Two teams are considered equal if they share the same UUID
 */
@Getter
@RequiredArgsConstructor
public class Team {

	/**
	 * The ID of the team, this will remain constant throughout the lifecycle of an individual team
	 */
	private final UUID id;

	private final TeamDetails teamDetails;

}
