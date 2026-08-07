package com.booksaw.betterteams.domain;

import static com.booksaw.betterteams.domain.utils.assertj.TeamDetailsAssert.assertTeamDetails;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

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
      .isInstanceOf(NullPointerException.class);
  }

}
