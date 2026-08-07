package com.booksaw.betterteams.domain;

public enum TeamJoinStatus {
	/**
	 * Any player can join the team
	 */
	OPEN,

	/**
	 * Players can only join the team if they have been sent an invite
	 */
	INVITE_ONLY
}
