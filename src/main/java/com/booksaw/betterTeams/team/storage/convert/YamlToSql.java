package com.booksaw.betterTeams.team.storage.convert;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Utils;
import com.booksaw.betterTeams.database.BetterTeamsDatabase;
import com.booksaw.betterTeams.database.TableName;
import com.booksaw.betterTeams.team.storage.storageManager.YamlStorageManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class YamlToSql extends Converter {

	@Override
	protected void convert() {

		File teams = new File("plugins/BetterTeams/" + YamlStorageManager.TEAMLISTSTORAGELOC + ".yml");
		if (!teams.exists()) {
			Main.plugin.saveResource("teams.yml", false);
		}

		YamlConfiguration teamStorage = YamlConfiguration.loadConfiguration(teams);

		log("Backing up current /BetterTeams/ folder");
		try {
			pack("plugins/BetterTeams/", "plugins/BetterTeamsBACKUP.zip");
		} catch (IOException e) {
			log("backup failed, aborting");
			e.printStackTrace();
			return;
		}
		log("Backup complete, starting conversion");
		log("establishing connection with the database");
		BetterTeamsDatabase database = new BetterTeamsDatabase();
		database.setupConnectionFromConfiguration(Main.plugin.getConfig().getConfigurationSection("database"));
		database.setupTables();
		log("connection esablished, continuing");

		// converting all teams
		File folder = new File("plugins/BetterTeams/teamInfo");
		int size = folder.listFiles().length;
		int current = 0;

		HashMap<String, String> allies = new HashMap<>();
		HashMap<String, String> allyRequests = new HashMap<>();

		for (File f : folder.listFiles()) {

			if (!f.getName().endsWith(".yml")) {
				continue;
			}
			UUID teamName = UUID.fromString(f.getName().replace(".yml", ""));
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

			Inventory inv = Bukkit.createInventory(null, 27);
			getEchestContents(inv, config);

			String echest = Utils.serializeInventory(inv);
			echest = echest.replace("\"", "\\\"");
			database.insertRecordIfNotExists(TableName.TEAM,
					"teamID, name, description, open, score, money, home, color, level, tag, pvp, anchor",
					"'" + teamName + "', '" + config.getString("name") + "', '" + config.getString("description") + "', "
							+ config.getBoolean("open") + ", " + config.getInt("score") + ", "
							+ config.getDouble("money") + ", '" + config.getString("home") + "', '"
							+ config.getString("color") + "', " + config.getInt("level") + ", '"
							+ config.getString("tag") + "', " + config.getBoolean("pvp") + ", "
							+ config.getBoolean("anchor", false));

			if (echest != null && !echest.isEmpty()) {
				database.updateRecordWhere(TableName.TEAM, "echest = \"" + echest + "\"",
						"teamID LIKE '" + teamName + "'");
			}

			// allies
			for (String temp : config.getStringList("allies")) {
				allies.put(teamName.toString(), temp);
			}
			// ally requests
			for (String temp : config.getStringList("allyrequests")) {
				allyRequests.put(teamName.toString(), temp);

			}
			// bans
			for (String temp : config.getStringList("bans")) {
				database.insertRecordIfNotExists(TableName.BANS, "teamID, playerUUID", "'" + teamName + "', '" + temp + "'");
			}
			// players
			List<String> anchoredPlayers = config.getStringList("anchoredPlayers");
			for (String temp : config.getStringList("players")) {
				String[] split = temp.split(",");
				PlayerRank rank = PlayerRank.getRank(split[1]);
				boolean anchor = anchoredPlayers.contains(split[0]);
				if (split.length == 2) {
					database.insertRecordIfNotExists(TableName.PLAYERS, "teamID, playerUUID, playerRank, anchor",
							"'" + teamName + "', '" + split[0] + "', " + rank.value + ", " + anchor);
				} else {
					database.insertRecordIfNotExists(TableName.PLAYERS, "teamID, playerUUID, playerRank, title, anchor",
							"'" + teamName + "', '" + split[0] + "', " + rank.value + ", '" + split[2] + "'" + ", " + anchor);
				}

			}
			// warps
			for (String temp : config.getStringList("warps")) {
				database.insertRecordIfNotExists(TableName.WARPS, "teamID, warpInfo", "'" + teamName + "', '" + temp + "'");
			}

			current++;
			log("Saved team " + teamName + "(" + current + "/" + size + ")");
			f.delete();
		}
		log("Converting allies, There may be error messages in this section, they are expected just ignore them");
		for (Entry<String, String> temp : allies.entrySet()) {
			database.insertRecord(TableName.ALLIES, "team1ID, team2ID",
					"'" + temp.getKey() + "', '" + temp.getValue() + "'");
		}
		for (Entry<String, String> temp : allyRequests.entrySet()) {
			database.insertRecord(TableName.ALLYREQUESTS, "receivingTeamID, requestingTeamID",
					"'" + temp.getKey() + "', '" + temp.getValue() + "'");
		}

		// chest claims
		log("converting chest claims");
		for (String temp : teamStorage.getStringList("chestClaims")) {
			String[] split = temp.split(";");
			database.insertRecord(TableName.CHESTCLAIMS, "teamID, chestLoc", "'" + split[1] + "', '" + split[0] + "'");
		}
		log("chest claims converted");

		teamStorage.set("storageType", "SQL");

		teamStorage.set("teamNameLookup", null);
		teamStorage.set("playerLookup", null);
		teamStorage.set("chestClaims", null);

		try {
			teamStorage.save(teams);
		} catch (IOException e) {
			Main.plugin.getLogger()
					.warning("Something went wrong with convering the config, REPORT THIS TO BOOKSAW");
			e.printStackTrace();
		}
	}

	public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
		File f = new File(zipFilePath);

		Path p = Paths.get(zipFilePath);

		if (!f.exists()) {
			Files.createFile(p);
		}

		try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
			Path pp = Paths.get(sourceDirPath);

			try (Stream<Path> paths = Files.walk(pp)) {
				paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
					ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
					try {
						zs.putNextEntry(zipEntry);
						Files.copy(path, zs);
						zs.closeEntry();
					} catch (IOException e) {
						System.err.println(e);
					}
				});
			}
		}
	}

	public void getEchestContents(Inventory inventory, YamlConfiguration section) {

		for (int i = 0; i < 27; i++) {
			ItemStack is = section.getItemStack("echest." + i);
			if (is != null) {
				inventory.setItem(i, is);
			}
		}
	}

}
