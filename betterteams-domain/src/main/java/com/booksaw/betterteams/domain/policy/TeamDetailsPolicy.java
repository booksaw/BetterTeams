package com.booksaw.betterteams.domain.policy;

import static com.booksaw.betterteams.domain.utils.PolicyValidationUtils.checkArgument;

import lombok.experimental.FieldNameConstants;

/**
 * Representation of all configurable properties to define properties of a team
 */
@FieldNameConstants
public record TeamDetailsPolicy(
  int minimumTeamNameLength,
  int maximumTeamNameLength
) {

  public TeamDetailsPolicy {
    checkArgument(minimumTeamNameLength > 0, "'%s' must be greater than 0",
      Fields.minimumTeamNameLength);
    checkArgument(minimumTeamNameLength < 30, "'%s' must be less than 30",
      Fields.maximumTeamNameLength);
  }

}
