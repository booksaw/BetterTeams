package dev.booksaw.betterteams.persistence.database.migration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Used to represent a database migration, migrations are applied in order of version from smallest to largest
 */
public interface DatabaseMigration {

	/**
	 * Apply the database migration here, should only ever be called once. Database transaction is managed outside of this migration class
	 *
	 * @param connection database connection
	 */
	void applyMigration(Connection connection) throws SQLException;

	/**
	 * @return version ID of the migration this class contains
	 */
	int getMigrationVersion();

}
