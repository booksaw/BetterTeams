package dev.booksaw.betterteams.domain.utils.assertj;

import dev.booksaw.betterteams.domain.TeamDetails;
import dev.booksaw.betterteams.domain.TeamJoinStatus;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class TeamDetailsAssert extends AbstractAssert<TeamDetailsAssert, TeamDetails> {

	protected TeamDetailsAssert(TeamDetails actual) {
		super(actual, TeamDetailsAssert.class);
	}

	public static TeamDetailsAssert assertTeamDetails(TeamDetails actual) {
		return new TeamDetailsAssert(actual);
	}

	public TeamDetailsAssert hasName(String expected) {
		isNotNull();
		assertThat(actual.getName()).isEqualTo(expected);
		return this;
	}

	public TeamDetailsAssert hasDescription(String expected) {
		isNotNull();
		assertThat(actual.getDescription()).isEqualTo(expected);
		return this;
	}

	public TeamDetailsAssert hasJoinStatus(TeamJoinStatus expected) {
		isNotNull();
		assertThat(actual.getJoinStatus()).isEqualTo(expected);
		return this;
	}
}
