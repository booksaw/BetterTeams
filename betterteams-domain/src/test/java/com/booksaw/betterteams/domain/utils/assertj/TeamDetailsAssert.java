package com.booksaw.betterteams.domain.utils.assertj;

import static org.assertj.core.api.Assertions.assertThat;

import com.booksaw.betterteams.domain.TeamDetails;
import com.booksaw.betterteams.domain.TeamJoinStatus;
import org.assertj.core.api.AbstractAssert;

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
