package com.booksaw.betterTeams.team.storage.convert;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.booksaw.betterTeams.ConfigManager;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.team.storage.storageManager.YamlStorageManager;
import com.booksaw.betterTeams.team.storage.team.SeparatedYamlTeamStorage;
import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;

public class FlatFileToYaml extends Converter {

	@Override
	protected void convert() {

		log("Backing up teams.yml file");

		File f = new File("plugins/BetterTeams/" + YamlStorageManager.TEAMLISTSTORAGELOC + ".yml");
		File copied = new File("plugins/BetterTeams/" + YamlStorageManager.TEAMLISTSTORAGELOC + "BACKUP.yml");

		if (!copied.exists()) {
			try {
				copied.createNewFile();
			} catch (IOException e) {
				log("could not create file to store backup in");
				log("There will be a bunch of errors and betterteams may not work but at least data will not be lost");
				e.printStackTrace();
				return;
			}
		}

		try (InputStream in = new BufferedInputStream(new FileInputStream(f));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(copied))) {

			byte[] buffer = new byte[1024];
			int lengthRead;
			while ((lengthRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, lengthRead);
				out.flush();
			}
		} catch (Exception e) {
			log("Could not save backup REPORT THIS ERROR TO BOOKSAW");
			log("There will be a bunch of errors and betterteams may not work but at least data will not be lost");
			e.printStackTrace();
			return;
		}

		log("Loading team information");

		if (!f.exists()) {
			Main.plugin.saveResource("teams.yml", false);
		}

		YamlConfiguration teamStorage = YamlConfiguration.loadConfiguration(f);

		List<String> teams = teamStorage.getStringList("teams");

		List<String> playerLookup = new ArrayList<>();
		List<String> teamLookup = new ArrayList<>();
		List<String> chestClaims = new ArrayList<>();

		int i = 1;

		log("Beginning convertion");

		for (String team : teams) {
			UUID teamUUID = UUID.fromString(team);
			ConfigManager configManager = new ConfigManager("teamInfo", SeparatedYamlTeamStorage.TEAMSAVEPATH + team);

			ConfigurationSection section = teamStorage.getConfigurationSection("team." + team);

			for (String key : section.getKeys(true)) {

				Object get = section.get(key);

				if (get instanceof ConfigurationSection) {
					continue;
				}

				configManager.config.set(key, get);
			}

			teamLookup.add(configManager.config.getString(StoredTeamValue.NAME.getReference()) + ":" + teamUUID);

			for (String playerUUID : configManager.config.getStringList("players")) {
				String[] split = playerUUID.split(",");
				playerLookup.add(UUID.fromString(split[0]) + ":" + teamUUID);
			}

			for (String claimLoc : configManager.config.getStringList("chests")) {
				chestClaims.add(claimLoc + ";" + teamUUID);
			}

			configManager.save(false);

			log("Saved team " + team + " (" + i + "/" + teams.size() + ")");
			i++;
		}

		teamStorage.set("storageType", "YAML");

		teamStorage.set("teamNameLookup", teamLookup);
		teamStorage.set("playerLookup", playerLookup);
		teamStorage.set("chestClaims", chestClaims);

		teamStorage.set("teams", null);
		teamStorage.set("team", null);

		try {
			teamStorage.save(f);
		} catch (IOException e) {
			Bukkit.getLogger()
					.warning("[BetterTeams] Something went wrong with convering the config, REPORT THIS TO BOOKSAW");
			e.printStackTrace();
		}

	}

}
