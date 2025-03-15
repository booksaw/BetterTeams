package com.booksaw.betterTeams.database;

import com.booksaw.betterTeams.Main;

public enum TableName {
	TEAM("Team"), PLAYERS("Players"), ALLYREQUESTS("AllyRequests"), WARPS("warps"), CHESTCLAIMS("ChestClaims"),
	BANS("Bans"), ALLIES("Allies");

	private final String tableName;

	TableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return Main.plugin.getConfig().getString("database.tablePrefix") + tableName;
	}

}
