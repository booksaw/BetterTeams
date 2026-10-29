package dev.booksaw.betterteams.domain;

import dev.booksaw.betterteams.domain.exception.InvalidPolicyConfigurationException;
import org.junit.jupiter.api.Test;

import static dev.booksaw.betterteams.domain.utils.assertj.TeamDetailsAssert.assertTeamDetails;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Responsible for testing the methods of {@link TeamDetails} are protected so the Team domain is
 * unable to be made invalid
 */
class TeamDetailsTest {

	@Test
	void valid_team_details_object_should_create() {
		TeamDetails details = TeamDetails.of("teamname", "this is a description", TeamJoinStatus.OPEN);

		assertTeamDetails(details)
				.hasName("teamname")
				.hasDescription("this is a description")
				.hasJoinStatus(TeamJoinStatus.OPEN);
	}

	@Test
	void null_join_status_should_throw() {
		assertThatThrownBy(() -> TeamDetails.of("name", "descr", null))
				.isInstanceOf(InvalidPolicyConfigurationException.class);
	}

	@Test
	void null_description_is_valid() {
		TeamDetails details = TeamDetails.of("name", null, TeamJoinStatus.OPEN);

		assertTeamDetails(details).hasDescription(null);
	}

	@Test
	void null_team_name_throws() {
		assertThatThrownBy(() -> TeamDetails.of(null, "descr", TeamJoinStatus.OPEN))
				.isInstanceOf(InvalidPolicyConfigurationException.class);
	}

	@Test
	void blank_team_name_throws() {
		assertThatThrownBy(() -> TeamDetails.of(" ", "descr", TeamJoinStatus.OPEN))
				.isInstanceOf(InvalidPolicyConfigurationException.class);
	}

	@Test
	void blank_description_throws() {
		assertThatThrownBy(() -> TeamDetails.of("name", " ", TeamJoinStatus.OPEN))
				.isInstanceOf(InvalidPolicyConfigurationException.class);
	}

}
