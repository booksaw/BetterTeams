package com.booksaw.betterTeams.database.api;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.database.TableName;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

/**
 * API Class to managing databases
 *
 * @author booksaw
 */
public class Database {

	/**
	 * Stores the information to create a connection to the database
	 */
	private String host;
	private int port;
	private String database;
	private String user;
	private String password;

	Connection connection;

	/**
	 * Used to setup a connection from the provided data
	 *
	 * @param section The configuration section which contains the database
	 *                information
	 */
	public void setupConnectionFromConfiguration(ConfigurationSection section) {

		host = section.getString("host", "localhost");
		port = section.getInt("port", 3306);
		database = section.getString("database", "spigot");
		user = section.getString("user", "root");
		password = section.getString("password", "password");

		setupConnection();

	}

	/**
	 * Used to setup a connection from the provided data
	 */
	public void setupConnection() {

		Main.plugin.getLogger().info("Attempting to connect to database");

		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", user,
					password);
			testDataSource();

		} catch (Exception e) {
			database = null;
			for (int i = 0; i < 3; i++) {
				Main.plugin.getLogger().severe("");
			}
			Main.plugin.getLogger()
					.severe("Connection to the database could not be established, disabling plugin");

			Main.plugin.getLogger().severe(
					"To use BetterTeams either change the storage type (config.yml/storageType) or correct the database credentials");

			for (int i = 0; i < 3; i++) {
				Main.plugin.getLogger().severe("");
			}

			e.printStackTrace();

			Main.plugin.getServer().getPluginManager().disablePlugin(Main.plugin);
			return;
		}

		Main.plugin.getLogger().info("Connection with the database established");

	}

	/*
	 * This is a bit of a hacky fix - you should look into connection pooling as a
	 * more permanent solution. A popular library is HikariCP
	 * (https://github.com/brettwooldridge/HikariCP)
	 */
	private void resetConnection() {
		if (connection == null) {
			throw new IllegalStateException("No SQL connection has been established");
		}

		try {
			connection.close();
			// Also, just a suggestion, but it's not recommended to use autoReconnect=true
			// as per the MySQL Connector/J developer docs.
			// https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-connp-props-high-availability-and-clustering.html
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", user,
					password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used to test if the connection is valid
	 *
	 * @throws SQLException If a connection cannot be established, this error will
	 *                      be thrown
	 */
	private void testDataSource() throws Exception {
		if (connection == null || connection.isClosed()) {
			throw new Exception("SQL connection is not setup correctly");
		}
	}

	/**
	 * Used to close the connection to the database
	 */
	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			Main.plugin.getLogger().severe("There was an error closing the database connection");
			e.printStackTrace();
		}
	}

	/**
	 * Used to execute an SQL statement
	 *
	 * @param statement    The SQL statement to execute
	 * @param placeholders The placeholders for the statement
	 */
	public void executeStatement(String statement, String... placeholders) {
		List<String> statementChars = Arrays.asList(statement.split(""));
		for (String placeholder : placeholders) {
			try {
				int index = statementChars.indexOf("?");
				if (index == -1) {
					throw new IndexOutOfBoundsException();
				}
				statementChars.set(index, placeholder);
			} catch (IndexOutOfBoundsException e) {
				Main.plugin.getLogger().severe("Invalid setup for replacing placeholders");
				Main.plugin.getLogger().severe("Statement: " + statement);
				Main.plugin.getLogger().severe("Placeholders: " + Arrays.toString(placeholders));
				e.printStackTrace();
			}
		}
		statement = String.join("", statementChars);
		statement = statement.replace("'false'", "false");
		statement = statement.replace("'true'", "true");

		try {
			if (!connection.isValid(2)) {
				resetConnection();
			}
		} catch (SQLException e) {
			// this error is never thrown as the timeout is > 0
		}

		try (PreparedStatement ps = connection.prepareStatement(statement)) {
			ps.executeUpdate();
		} catch (SQLException e) {
			Main.plugin.getLogger().severe("Something went wrong while executing SQL");
			Main.plugin.getLogger().severe("SQL: " + statement);
			e.printStackTrace();
		}
	}

	/**
	 * Used to execute an sql query
	 *
	 * @param query        The query to execute
	 * @param placeholders The placeholders within that query
	 * @return The results of the query
	 */
	public PreparedStatement executeQuery(String query, String... placeholders) {
		for (String placeholder : placeholders) {
			query = query.replaceFirst("\\?", placeholder);
		}

		try {
			if (!connection.isValid(2)) {
				resetConnection();
			}
			return connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			Main.plugin.getLogger().severe("Something went wrong while executing SQL");
			Main.plugin.getLogger().severe("SQL: " + query);
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Used to create a table if the table does not currently exist
	 *
	 * @param tableName The name of the table
	 * @param tableInfo The column information about the table
	 */
	public void createTableIfNotExists(TableName tableName, String tableInfo) {
		executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + "(" + tableInfo + ");");
	}
}
