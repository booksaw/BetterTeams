package com.booksaw.betterTeams.team.storage.storageManager;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.team.TeamManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
		File backF = new File("plugins/BetterTeams/" + TEAMLISTSTORAGELOC + ".yml.bak");
		File preF = new File("plugins/BetterTeams/" + TEAMLISTSTORAGELOC + ".yml.pre");
		File f = new File("plugins/BetterTeams/" + TEAMLISTSTORAGELOC + ".yml");

		try {
			Files.copy(f.toPath(), backF.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			Main.plugin.getLogger().log(Level.SEVERE, "Could not save backup to " + backF, ex);
			return;
		}

		try {
			teamStorage.save(preF);
		} catch (IOException ex) {
			Main.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
			return;
		}

		try {
			Files.move(preF.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			Main.plugin.getLogger().log(Level.SEVERE, "Could not move " + preF + " to " + f, ex);
			return;
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
