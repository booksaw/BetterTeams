package dev.booksaw.betterteams.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static dev.booksaw.betterteams.domain.utils.PolicyValidationUtils.checkArgument;
import static dev.booksaw.betterteams.domain.utils.PolicyValidationUtils.checkNotNull;

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
		entity.updateName(name);
		entity.updateDescription(description);
		entity.updateTeamJoinStatus(joinStatus);
		return entity;
	}

	/**
	 * The name of the team, this can be changed after the creation of a teams, so
	 * do not store references to it
	 */
	private String name;

	/**
	 * Description of the team. Can be null, cannot be blank
	 */
	private String description;

	/**
	 * The joining state of the team
	 */
	private TeamJoinStatus joinStatus;

	public void updateName(String name) {
		checkNotNull(name, "name");
		checkArgument(!name.isBlank(), "'name' cannot be blank");

		this.name = name;
	}

	public void updateDescription(String description) {
		if (description == null) {
			this.description = null;
			return;
		}

		checkArgument(!description.isBlank(), "'name' cannot be blank");
		this.description = description;
	}

	public void updateTeamJoinStatus(TeamJoinStatus joinStatus) {
		checkNotNull(joinStatus, "join status");
		this.joinStatus = joinStatus;
	}
}
