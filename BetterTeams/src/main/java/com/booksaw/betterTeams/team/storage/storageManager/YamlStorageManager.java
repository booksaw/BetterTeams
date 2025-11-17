package com.booksaw.betterTeams.team.storage.storageManager;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.team.TeamManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

@Getter
public abstract class YamlStorageManager extends TeamManager {

	protected final FileConfiguration teamStorage;

	public static final String TEAMLISTSTORAGELOC = "teams";

	protected YamlStorageManager() {
		// loading the teamStorage variable
		File f = new File("plugins/BetterTeams/" + TEAMLISTSTORAGELOC + ".yml");

		if (!f.exists()) {
			Main.plugin.saveResource("teams.yml", false);
		}

		teamStorage = YamlConfiguration.loadConfiguration(f);

	}

	public void saveTeamsFile() {
		File f = new File("plugins/BetterTeams/" + TEAMLISTSTORAGELOC + ".yml");
		try {
			teamStorage.save(f);
		} catch (IOException ex) {
			Main.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
		}
	}

	private static final String HOLOPATH = "holos";

	@Override
	public List<String> getHoloDetails() {
		return teamStorage.getStringList(HOLOPATH);
	}

	@Override
	public void setHoloDetails(List<String> details) {
		teamStorage.set(HOLOPATH, details);
		saveTeamsFile();
	}

}
