package dev.booksaw.betterteams.persistence.database.sqlite;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLiteConsumer<T> {

	void accept(T value) throws SQLException;
}
