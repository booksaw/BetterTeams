package dev.booksaw.betterteams.persistence.database.sqlite;

import dev.booksaw.betterteams.domain.Team;
import dev.booksaw.betterteams.persistence.database.TeamStorage;
import dev.booksaw.betterteams.persistence.database.migration.SQLiteMigrationV1;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;

public class SQLiteStorage implements TeamStorage {

	private final Path databasePath;

	private Connection connection;

	public SQLiteStorage(Path datafolder) {
		this.databasePath = datafolder.resolve("teams.db");

	}

	@Override
	public void initialize() throws SQLException {
		createDataDirectory();
		connect();
		configure();
		migrate();
	}

	@Override
	public void saveTeam(Team team) throws SQLException {
		executeWithinTransaction(
				connection -> {
					// Save team data
					try (PreparedStatement statement = connection.prepareStatement("""
						INSERT INTO team (name, )
					"""))
				});
	}

	public Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			throw new IllegalStateException("Connection has not been initialized");
		}
		return connection;
	}

	public void executeWithinTransaction(SQLiteConsumer<Connection> consumer) throws SQLException {
		boolean previousAutocommit = connection.getAutoCommit();

		try {
			connection.setAutoCommit(false);
			consumer.accept(connection);
			connection.commit();
		} catch (SQLException | RuntimeException e) {
			connection.rollback();
			throw e;
		} finally {
			connection.setAutoCommit(false);
		}
	}

	private void createDataDirectory() throws SQLException {
		try {
			Files.createDirectories(databasePath.getParent());
		} catch (Exception e) {
			throw new SQLException("Failed to create database directory", e);
		}
	}


	private void connect() throws SQLException {
		String url = "jdbc:sqlite:" + databasePath;
		connection = DriverManager.getConnection(url);
	}

	private void configure() throws SQLException {
		try (Statement statement = connection.createStatement()) {
			// Enforce foreign key constraints.
			statement.execute("PRAGMA foreign_keys = ON");

			// Use WAL mode for better read/write concurrency.
			statement.execute("PRAGMA journal_mode = WAL");

			// Wait for a short period if the database is locked.
			statement.execute("PRAGMA busy_timeout = 5000");
		}
	}

	private void migrate() throws SQLException {
		// Add new SQL migrations to this list
		SQLiteMigrationRunner migrationRunner = new SQLiteMigrationRunner(
				this,
				List.of(
						new SQLiteMigrationV1()
				));

		migrationRunner.migrate();
	}
}
