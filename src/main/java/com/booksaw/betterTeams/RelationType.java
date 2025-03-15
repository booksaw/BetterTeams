package com.booksaw.betterTeams;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RelationType {
	ALLY("ally"),
	NEUTRAL("neutral"),
	;

	private final String name;
}
