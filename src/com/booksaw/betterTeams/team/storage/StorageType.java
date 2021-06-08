package com.booksaw.betterTeams.team.storage;

import com.booksaw.betterTeams.team.storage.storageManager.TeamStorageManager;

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
	YAML;

	/**
	 * @return the teamStorageManager relevant to the storageType
	 */
	public TeamStorageManager getNewStorageManager() {
		switch (this) {
		case FLATFILE:
			// TODO
			return null;
		case YAML:
			// TODO
			return null;
		default:
			return null;
		}
	}
}
