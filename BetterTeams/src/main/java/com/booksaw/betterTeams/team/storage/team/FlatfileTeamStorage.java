package com.booksaw.betterTeams.team.storage.team;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.storage.storageManager.FlatfileStorageManager;
import org.bukkit.configuration.ConfigurationSection;

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

	@Override
	protected void saveFile() {
		teamStorage.saveTeamsFile();
	}

}
