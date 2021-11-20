package com.booksaw.betterTeams.database;

import com.booksaw.betterTeams.database.api.Database;

public class BetterTeamsDatabase extends Database {

	public void setupTables() {
		createTableIfNotExists(TableName.TEAM.toString(),
				"teamID VARCHAR(50) NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL, description VARCHAR(200), open BOOLEAN NOT NULL, score INT DEFAULT 0, money DOUBLE DEFAULT 0, home VARCHAR(50), color CHAR(1) DEFAULT '6', echest VARCHAR(50), level INT DEFAULT 1, tag VARCHAR(50)");

		createTableIfNotExists(TableName.PLAYERS.toString(),
				"playerUUID VARCHAR(50) NOT NULL PRIMARY KEY, teamID VARCHAR(50) NOT NULL, playerRank INT NOT NULL, FOREIGN KEY (teamID) REFERENCES "
						+ TableName.TEAM.toString() + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.ALLYREQUESTS.toString(),
				"requestingTeamID VARCHAR(50) NOT NULL, receivingTeamID VARCHAR(50) NOT NULL, PRIMARY KEY(requestingTeamID, receivingTeamID), FOREIGN KEY (requestingTeamID) REFERENCES "
						+ TableName.TEAM.toString()
						+ "(teamID) ON DELETE CASCADE, FOREIGN KEY (receivingTeamID) REFERENCES "
						+ TableName.TEAM.toString() + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.WARPS.toString(),
				"TeamID VARCHAR(50) NOT NULL, warpInfo VARCHAR(50) NOT NULL, PRIMARY KEY(TeamID, warpInfo), FOREIGN KEY (TeamID) REFERENCES "
						+ TableName.TEAM.toString()
						+ "(teamID) ON DELETE CASCADE");
		
		createTableIfNotExists(TableName.CHESTCLAIMS.toString(),
				"TeamID VARCHAR(50) NOT NULL, chestLoc VARCHAR(50) NOT NULL, PRIMARY KEY(TeamID, chestLoc), FOREIGN KEY (TeamID) REFERENCES "
						+ TableName.TEAM.toString()
						+ "(teamID) ON DELETE CASCADE");
		
		createTableIfNotExists(TableName.BANS.toString(),
				"PlayerUUID VARCHAR(50) NOT NULL, TeamID VARCHAR(50) NOT NULL, PRIMARY KEY(PlayerUUID, TeamID), FOREIGN KEY (teamID) REFERENCES "
						+ TableName.TEAM.toString()
						+ "(teamID) ON DELETE CASCADE");
		
		createTableIfNotExists(TableName.ALLIES.toString(),
				"team1ID VARCHAR(50) NOT NULL, team2ID VARCHAR(50) NOT NULL, PRIMARY KEY(team1ID, team2ID), FOREIGN KEY (team1ID) REFERENCES "
						+ TableName.TEAM.toString()
						+ "(teamID) ON DELETE CASCADE, FOREIGN KEY (team2ID) REFERENCES "
						+ TableName.TEAM.toString() + "(teamID) ON DELETE CASCADE");
		
	}

}
