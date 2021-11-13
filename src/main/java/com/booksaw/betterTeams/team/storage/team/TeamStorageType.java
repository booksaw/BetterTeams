package com.booksaw.betterTeams.team.storage.team;

public enum TeamStorageType {

	STRING(String.class), INTEGER(Integer.class), DOUBLE(Double.class), BOOLEAN(Boolean.class);

	private final Class<?> storageClass;

	private TeamStorageType(Class<?> storageClass) {
		this.storageClass = storageClass;
	}

	public Class<?> getStorageClass() {
		return storageClass;
	}

}
