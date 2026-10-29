package dev.booksaw.betterteams.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTestUtils {

	public static Connection createDatabase() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");

		try (Statement statement = connection.createStatement()) {
			statement.execute("PRAGMA foreign_keys = ON");
		}

		return connection;
	}

	public static void setVersion(Connection connection, int version) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("PRAGMA user_version = " + version);
		}
	}

}
