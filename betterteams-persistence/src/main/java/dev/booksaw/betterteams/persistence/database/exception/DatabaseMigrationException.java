package dev.booksaw.betterteams.persistence.database.exception;

public class DatabaseMigrationException extends RuntimeException {

	public DatabaseMigrationException(String message, Throwable e) {
		super(message, e);
	}

}
