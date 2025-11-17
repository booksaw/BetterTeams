package com.booksaw.betterTeams.team.storage;

import com.booksaw.betterTeams.team.TeamManager;
import com.booksaw.betterTeams.team.storage.storageManager.FlatfileStorageManager;
import com.booksaw.betterTeams.team.storage.storageManager.SQLStorageManager;
import com.booksaw.betterTeams.team.storage.storageManager.SeparatedYamlStorageManager;

public enum StorageType {

	/**
	 * FLATFILE is the storage method pre 4.0 where all team data is stored within a
	 * single file
	 */
	FLATFILE,

	/**
	 * YAML is where team data is all stored in individual team files along with
	 * teams.yml to contain pointers to the correct file
	 */
	YAML,

	/**
	 * SQL is where team data is stored in a mySQL database
	 */
	SQL;

	/**
	 * @return the teamStorageManager relevant to the storageType
	 */
	public TeamManager getNewTeamManager() {
		switch (this) {
			case FLATFILE:
				return new FlatfileStorageManager();
			case YAML:
				return new SeparatedYamlStorageManager();
			case SQL:
				return new SQLStorageManager();
			default:
				return new SeparatedYamlStorageManager();
		}
	}

	public static StorageType getStorageType(String str) {
		switch (str.toUpperCase()) {
			case "FLATFILE":
				return FLATFILE;
			case "YAML":
				return YAML;
			case "SQL":
				return SQL;
			default:
				return YAML;
		}
	}

}
