package dev.booksaw.betterteams.persistence.sqlite;

import dev.booksaw.betterteams.persistence.SQLiteTestUtils;
import dev.booksaw.betterteams.persistence.database.exception.DatabaseMigrationException;
import dev.booksaw.betterteams.persistence.database.migration.DatabaseMigration;
import dev.booksaw.betterteams.persistence.database.sqlite.SQLiteMigrationRunner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static dev.booksaw.betterteams.persistence.utils.assertj.SQLiteDatabaseAssert.assertSqliteDatabase;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SQLiteMigrationRunnerTest {

	@Test
	void run_single_migration() throws Exception {
		try (Connection connection = SQLiteTestUtils.createDatabase()) {
			DatabaseMigration migration = new TestMigration(1);

			var migrationRunner = new SQLiteMigrationRunner(connection, List.of(migration));
			migrationRunner.migrate();

			assertSqliteDatabase(connection)
					.hasMigrationVersion(1)
					.hasTable("test_table_1");
		}
	}

	@Test
	void run_multiple_migrations() throws Exception {
		try (Connection connection = SQLiteTestUtils.createDatabase()) {
			DatabaseMigration migration1 = new TestMigration(1);
			DatabaseMigration migration2 = new TestMigration(2);
			DatabaseMigration migration3 = new TestMigration(3);

			var migrationRunner = new SQLiteMigrationRunner(connection, List.of(migration1, migration2, migration3));
			migrationRunner.migrate();

			assertSqliteDatabase(connection)
					.hasMigrationVersion(3)
					.hasTable("test_table_1")
					.hasTable("test_table_2")
					.hasTable("test_table_3");
		}
	}

	@Test
	void run_migration_after_setting_version() throws Exception {
		try (Connection connection = SQLiteTestUtils.createDatabase()) {
			// Given 3 migrations
			DatabaseMigration migration1 = new TestMigration(1);
			DatabaseMigration migration2 = new TestMigration(2);
			DatabaseMigration migration3 = new TestMigration(3);

			// And the database is registered that it is already running version 1
			SQLiteTestUtils.setVersion(connection, 1);

			// When I migrate
			var migrationRunner = new SQLiteMigrationRunner(connection, List.of(migration1, migration2, migration3));
			migrationRunner.migrate();

			// Then only the second and third migration are run

			assertSqliteDatabase(connection)
					.hasMigrationVersion(3)
					.tableDoesNotExist("test_table_1")
					.hasTable("test_table_2")
					.hasTable("test_table_3");
		}
	}

	@Test
	void run_failed_migration_rolls_back() throws Exception {
		try (Connection connection = SQLiteTestUtils.createDatabase()) {
			// Given a migration which will fail
			DatabaseMigration migration = new FailedMigration(1);

			// When I migrate an exception is thrown
			var migrationRunner = new SQLiteMigrationRunner(connection, List.of(migration));

			assertThatThrownBy(migrationRunner::migrate)
					.isInstanceOf(DatabaseMigrationException.class);

			// and the version has not been marked as completed
			assertSqliteDatabase(connection)
					.hasMigrationVersion(0)
					.tableDoesNotExist("test_table_1");
		}
	}

	@RequiredArgsConstructor
	@Getter
	private static final class TestMigration implements DatabaseMigration {

		private final int migrationVersion;

		@Override
		public void applyMigration(Connection connection) throws SQLException {
			try (Statement statement = connection.createStatement()) {
				statement.execute("""
						CREATE TABLE test_table_%d (
							id INTEGER PRIMARY KEY
						)""".formatted(migrationVersion));
			}
		}

	}

	@RequiredArgsConstructor
	@Getter
	private static final class FailedMigration implements DatabaseMigration {

		private final int migrationVersion;

		@Override
		public void applyMigration(Connection connection) throws SQLException {
			try (Statement statement = connection.createStatement()) {
				statement.execute("""
						CREATE TABLE test_table_%d (
							id INTEGER PRIMARY KEY
						)""".formatted(migrationVersion));
			}
			throw new SQLException("ERROR!!!");
		}

	}
}
