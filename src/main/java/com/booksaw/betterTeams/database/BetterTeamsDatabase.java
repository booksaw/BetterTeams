package com.booksaw.betterTeams.database;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.database.api.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BetterTeamsDatabase extends Database {

	public void setupTables() {
		createTableIfNotExists(TableName.TEAM,
				"teamID VARCHAR(50) NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL, description VARCHAR(300), open BOOLEAN DEFAULT 0, score INT DEFAULT 0, money DOUBLE DEFAULT 0, home VARCHAR(200), color CHAR(1) DEFAULT '6', echest TEXT(20000), level INT DEFAULT 1, tag VARCHAR(50), pvp BOOLEAN DEFAULT 0");

		createTableIfNotExists(TableName.PLAYERS,
				"playerUUID VARCHAR(50) NOT NULL PRIMARY KEY, teamID VARCHAR(50) NOT NULL, playerRank INT NOT NULL, title VARCHAR(100), FOREIGN KEY (teamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.ALLYREQUESTS,
				"requestingTeamID VARCHAR(50) NOT NULL, receivingTeamID VARCHAR(50) NOT NULL, PRIMARY KEY(requestingTeamID, receivingTeamID), FOREIGN KEY (requestingTeamID) REFERENCES "
						+ TableName.TEAM
						+ "(teamID) ON DELETE CASCADE, FOREIGN KEY (receivingTeamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.WARPS,
				"TeamID VARCHAR(50) NOT NULL, warpInfo VARCHAR(200) NOT NULL, PRIMARY KEY(TeamID, warpInfo), FOREIGN KEY (TeamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.CHESTCLAIMS,
				"TeamID VARCHAR(50) NOT NULL, chestLoc VARCHAR(50) NOT NULL, PRIMARY KEY(TeamID, chestLoc), FOREIGN KEY (TeamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.BANS,
				"PlayerUUID VARCHAR(50) NOT NULL, TeamID VARCHAR(50) NOT NULL, PRIMARY KEY(PlayerUUID, TeamID), FOREIGN KEY (teamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.ALLIES,
				"team1ID VARCHAR(50) NOT NULL, team2ID VARCHAR(50) NOT NULL, PRIMARY KEY(team1ID, team2ID), FOREIGN KEY (team1ID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE, FOREIGN KEY (team2ID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		// Add new anchor columns
        addAnchorColumnIfNeeded(TableName.TEAM);
        addAnchorColumnIfNeeded(TableName.PLAYERS);
	}

	private void addAnchorColumnIfNeeded(TableName tableName) {
        String checkColumnQuery = "SHOW COLUMNS FROM " + tableName + " LIKE 'anchor';";
        String alterTableQuery = "ALTER TABLE " + tableName + " ADD COLUMN anchor BOOLEAN DEFAULT 0;";

        PreparedStatement ps = executeQuery(checkColumnQuery);
		try {
			if (!ps.executeQuery().next()) { // No result means non existent table
				executeStatement(alterTableQuery);
			}
		} catch (SQLException e) {
            Main.plugin.getLogger().severe("Could not set 'anchor' column in table" + tableName);
			e.printStackTrace();
        }
    }

	public PreparedStatement select(String select, TableName from) {
		return executeQuery("SELECT ? FROM ?", select, from.toString());
	}

	/**
	 * @param select the element to select
	 * @param from   the table which the data is from
	 * @param where  the condition required for the data to be included
	 * @return the resultant resultSet
	 */
	public PreparedStatement selectWhere(String select, TableName from, String where) {
		return executeQuery("SELECT ? FROM ? WHERE ?;", select, from.toString(), where);
	}

	/**
	 * @param select  the element to select
	 * @param from    the table which the data is from
	 * @param where   the condition required for the data to be included
	 * @param orderBy what to order the data by
	 * @return the resultant resultset
	 */
	public PreparedStatement selectWhereOrder(String select, TableName from, String where, String orderBy) {
		return executeQuery("SELECT ? FROM ? WHERE ? ORDER BY ?;", select, from.toString(), where, orderBy);
	}

	/**
	 * @param select  the element to select
	 * @param from    the table which the data is from
	 * @param orderBy what to order the data by
	 * @return The resultant resultset
	 */
	public PreparedStatement selectOrder(String select, TableName from, String orderBy) {
		return executeQuery("SELECT ? FROM ? ORDER BY ?;", select, from.toString(), orderBy);
	}

	/**
	 * @param select      the element to select
	 * @param table       the table which the data is from
	 * @param joinTable   the table to join
	 * @param columToJoin the details of the join
	 * @param orderBy     the order by conditions
	 * @return The resultSet of the select
	 */
	public PreparedStatement selectInnerJoinOrder(String select, TableName table, TableName joinTable, String columToJoin,
												  String orderBy) {
		return executeQuery("SELECT ? FROM ? INNER JOIN ? on (?) ORDER BY ?;", select, table.toString(),
				joinTable.toString(), columToJoin, orderBy);
	}

	public PreparedStatement selectInnerJoinGroupByOrder(String select, TableName table, TableName joinTable,
														 String columToJoin, String groupBy, String orderBy) {
		return executeQuery("SELECT ? FROM ? INNER JOIN ? on (?) GROUP BY ? ORDER BY ?;", select, table.toString(),
				joinTable.toString(), columToJoin, groupBy, orderBy);
	}

	/**
	 * Used to check if an SQL query has a result
	 *
	 * @param from  the table which the data is from
	 * @param where the condition required for the data to be included
	 * @return if the query has a result
	 */
	public boolean hasResult(TableName from, String where) {

		try (PreparedStatement ps = selectWhere("*", from, where)) {
			ResultSet result = ps.executeQuery();
			if (result == null) {
				return false;
			}
			return result.first();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Used to a specific column value from the
	 *
	 * @param column The column name
	 * @param from   the table
	 * @param where  the condition
	 * @return the first returned result, the specified column. Will return "" if an
	 * error occurs
	 */
	public String getResult(String column, TableName from, String where) {

		try (PreparedStatement pr = selectWhere(column, from, where)) {
			ResultSet results = pr.executeQuery();
			results.first();
			return results.getString(column);
		} catch (SQLException e) {
			return "";
		}

	}

	/**
	 * Used to delete a record from the specified table
	 *
	 * @param table     The table to update
	 * @param condition The condition for the update
	 */
	public void deleteRecord(TableName table, String condition) {
		executeStatement("DELETE FROM ? WHERE ?;", table.toString(), condition);

	}

	/**
	 * Used to modify a record in the database
	 *
	 * @param table     the table the record is in
	 * @param update    the values to update (ie "col = exp1, col2 = exp2")
	 * @param condition The condition for which records should be updated
	 */
	public void updateRecordWhere(TableName table, String update, String condition) {
		executeStatement("UPDATE ? SET ? WHERE ?;", table.toString(), update, condition);
	}

	/**
	 * Used to modify all records in a database
	 *
	 * @param table  the table to modify
	 * @param update the value to update (ie "col = exp1, col2 = exp2")
	 */
	public void updateRecord(TableName table, String update) {
		executeStatement("UPDATE ? SET ?", table.toString(), update);
	}

	/**
	 * Used to add a record into a table
	 *
	 * @param table   The table to insert the record into
	 * @param columns The columns that data is being provided for (ie "col1, col2,
	 *                col3)
	 * @param values  the values to insert (ie "val1, val2, val3")
	 */
	public void insertRecord(TableName table, String columns, String values) {
		executeStatement("INSERT INTO ? (?) VALUES (?);", table.toString(), columns, values);
	}

	public void insertRecordIfNotExists(TableName table, String columns, String values) {
//		executeStatement("IF NOT EXISTS (SELECT * FROM ? WHERE ?) INSERT INTO ? (?) VALUES(?);", table.toString(),
//				condition, table.toString(), columns, values);
		executeStatement("INSERT IGNORE INTO ? (?) VALUES (?);", table.toString(), columns, values);
	}

}
