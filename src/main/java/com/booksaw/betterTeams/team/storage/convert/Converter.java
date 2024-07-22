package com.booksaw.betterTeams.team.storage.convert;

import com.booksaw.betterTeams.team.storage.StorageType;
import org.bukkit.Bukkit;

public abstract class Converter {

	public static Converter getConverter(StorageType from, StorageType to) {

		if (from == to) {
			return null;
		}

		if (from == StorageType.FLATFILE) {
			if (to == StorageType.YAML) {
				return new FlatFileToYaml();
			}
		} else if (from == StorageType.YAML) {
			if (to == StorageType.SQL) {
				return new YamlToSql();
			}
		}

		return null;

	}

	protected void log(String message) {
		Bukkit.getLogger().info("[BetterTeams] " + message);
	}

	public void convertStorage() {
		log("Converting storage type, this may take a while");
		convert();
		log("Convertion complete");
	}

	protected abstract void convert();

}
