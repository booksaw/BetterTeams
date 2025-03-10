package com.booksaw.betterTeams.team.storage.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamStorageType {

	STRING(String.class), INTEGER(Integer.class), DOUBLE(Double.class), BOOLEAN(Boolean.class);

	private final Class<?> storageClass;
}
