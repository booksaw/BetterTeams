package dev.booksaw.betterteams.persistence.utils.assertj;

import org.assertj.core.api.AbstractAssert;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class SQLiteDatabaseAssert extends AbstractAssert<SQLiteDatabaseAssert, Connection> {

	protected SQLiteDatabaseAssert(Connection actual) {
		super(actual, SQLiteDatabaseAssert.class);
	}

	public static SQLiteDatabaseAssert assertSqliteDatabase(Connection actual) {
		return new SQLiteDatabaseAssert(actual);
	}

	public SQLiteDatabaseAssert hasMigrationVersion(int expected) throws SQLException {
		isNotNull();

		try (Statement statement = actual.createStatement()) {
			var result = statement.executeQuery("PRAGMA user_version");
			assertThat(result.getInt(1)).isEqualTo(expected);
		}

		return this;
	}

	public SQLiteDatabaseAssert hasTable(String expected) throws SQLException {
		isNotNull();

		try (var statement = actual.prepareStatement("""
				SELECT COUNT(*)
				FROM sqlite_master
				WHERE type = 'table'
				AND name = ?
				""")) {
			statement.setString(1, expected);

			try (var result = statement.executeQuery()) {
				assertThat(result.getInt(1)).isGreaterThan(0);
			}

		}

		return this;
	}

	public SQLiteDatabaseAssert tableDoesNotExist(String expected) throws SQLException {
		isNotNull();

		try (var statement = actual.prepareStatement("""
				SELECT COUNT(*)
				FROM sqlite_master
				WHERE type = 'table'
				AND name = ?
				""")) {
			statement.setString(1, expected);

			try (var result = statement.executeQuery()) {
				assertThat(result.getInt(1)).isEqualTo(0);
			}

		}

		return this;
	}
}
