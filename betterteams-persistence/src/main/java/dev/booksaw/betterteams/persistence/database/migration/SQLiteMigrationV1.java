package dev.booksaw.betterteams.persistence.database.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteMigrationV1 implements DatabaseMigration {

	@Override
	public void applyMigration(Connection connection) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			// Create the team table
			statement.execute("""
						CREATE TABLE team (
							id INTEGER PRIMARY KEY AUTOINCREMENT,
							name TEXT NOT NULL UNIQUE,
							description TEXT,
						)
					""");
		}
	}

	@Override
	public int getMigrationVersion() {
		return 1;
	}
}
