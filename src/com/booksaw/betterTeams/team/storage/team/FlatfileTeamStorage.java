package com.booksaw.betterTeams.team.storage.team;

import org.bukkit.configuration.ConfigurationSection;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.storage.storageManager.FlatfileStorageManager;

public class FlatfileTeamStorage extends YamlTeamStorage {

	public FlatfileTeamStorage(Team team, FlatfileStorageManager teamStorage) {
		super(team, teamStorage);
	}

	public ConfigurationSection getConfig() {
		ConfigurationSection section = teamStorage.getTeamStorage().getConfigurationSection(getConfigPath());
		if (section == null) {
			section = teamStorage.getTeamStorage().createSection(getConfigPath());
		}
		return section;
	}

	private String getConfigPath() {
		return "team." + team.getID();
	}

}
