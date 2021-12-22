package com.booksaw.betterTeams.database;

public enum TableName {
	TEAM("Team"), PLAYERS("Players"), ALLYREQUESTS("AllyRequests"), WARPS("warps"), CHESTCLAIMS("ChestClaims"),
	BANS("Bans"), ALLIES("Allies");

	private final String tableName;

	TableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "BetterTeams_" + tableName;
	}

}
