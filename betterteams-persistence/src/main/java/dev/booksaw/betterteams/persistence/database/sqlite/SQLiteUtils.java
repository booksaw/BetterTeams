package dev.booksaw.betterteams.persistence.database.sqlite;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteUtils {


	public interface SQLExecution {
		void execute(Connection connection) throws SQLException;
	}

}
