package com.booksaw.betterTeams.database.api;

public enum DataType {

	CHAR(true), VARCHAR(true), BOOLEAN, INT;

	public final boolean needArg;

	DataType() {
		needArg = false;
	}

	DataType(boolean needArg) {
		this.needArg = needArg;
	}

}
