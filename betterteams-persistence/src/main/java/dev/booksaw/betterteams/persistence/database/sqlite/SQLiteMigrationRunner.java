package dev.booksaw.betterteams.persistence.database.sqlite;

import dev.booksaw.betterteams.persistence.database.exception.DatabaseMigrationException;
import dev.booksaw.betterteams.persistence.database.migration.DatabaseMigration;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@RequiredArgsConstructor
public class SQLiteMigrationRunner {

	private final SQLiteStorage sqLiteStorage;

	private final List<DatabaseMigration> migrations;

	public void migrate() throws SQLException {
		int currentVersion = getVersion();

		for (DatabaseMigration migration : migrations) {
			if (migration.getMigrationVersion() <= currentVersion) {
				continue;
			}

			runMigration(migration);
			currentVersion = migration.getMigrationVersion();
		}
	}

	private void runMigration(DatabaseMigration migration) throws SQLException {
		try {
			sqLiteStorage.executeWithinTransaction(
					connection -> {
						migration.applyMigration(connection);
						setVersion(connection, migration.getMigrationVersion());
					}
			);
		} catch (SQLException e) {
			throw new DatabaseMigrationException("Database version '%d' failed".formatted(migration.getMigrationVersion()), e);
		}
	}

	private int getVersion() throws SQLException {
		try (Statement statement = sqLiteStorage.getConnection().createStatement()) {
			ResultSet result = statement.executeQuery(
					"PRAGMA user_version"
			);
			return result.getInt(1);
		}
	}

	private void setVersion(Connection connection, int version) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("PRAGMA user_version = " + version);
		}
	}
}
