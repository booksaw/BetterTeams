package com.booksaw.betterTeams.database.api;

/**
 * Class used to build a table from scratch NOTE THIS CLASS IS UNFINISHED
 * <p>
 * for example you cannot set the primary key of the table
 * </p>
 * 
 * @author booksaw
 *
 */
public class TableBuilder {

	private final Database database;
	private final String tableName;
	private final StringBuilder tableInfo = new StringBuilder();

	public TableBuilder(Database database, String tableName) {
		this.database = database;
		this.tableName = tableName;
	}

	/**
	 * @return The table info for this builder
	 */
	public String getTableInfo() {
		return tableInfo.substring(0, tableInfo.length() - 1);
	}

	/**
	 * @return The table name for this builder
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Execute the built command
	 */
	public void execute() {
		if (tableInfo.length() == 0) {
			return;
		}
		database.createTableIfNotExists(tableName, tableInfo.toString());
	}

	public TableBuilder addColumn(String columnName, DataType type) {
		return addColumn(columnName, type, false);
	}

	public TableBuilder addColumn(String columnName, DataType type, boolean notNull) {
		if (type.needArg) {
			throw new IllegalArgumentException(
					"The data type " + type + " needs an argument, and no argument was provided");
		}

		tableInfo.append(columnName + " " + type.toString() + ((notNull) ? " NOT NULL, " : ", "));
		return this;
	}

	public TableBuilder addColumn(String columnName, DataType type, String argument) {
		return addColumn(columnName, type, argument, false);
	}

	public TableBuilder addColumn(String columnName, DataType type, String argument, boolean notNull) {
		if (type.needArg) {
			throw new IllegalArgumentException(
					"The data type " + type + " needs an argument, and no argument was provided");
		}

		tableInfo
				.append(columnName + " " + type.toString() + "(" + argument + ")" + ((notNull) ? " NOT NULL, " : ", "));
		return this;
	}

}
