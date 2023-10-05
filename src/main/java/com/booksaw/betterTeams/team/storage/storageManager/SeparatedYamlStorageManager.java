package com.booksaw.betterTeams.team.storage.storageManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.team.LocationListComponent;
import com.booksaw.betterTeams.team.storage.team.SeparatedYamlTeamStorage;
import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class SeparatedYamlStorageManager extends YamlStorageManager implements Listener {

	private final Map<String, UUID> teamNameLookup = new HashMap<>();
	private final Map<UUID, UUID> playerLookup = new HashMap<>();
	private final Map<String, UUID> chestClaims = new HashMap<>();

	private final File teamStorageDir;

	public SeparatedYamlStorageManager() {
		super();

		teamStorageDir = SeparatedYamlTeamStorage.getTeamSaveFile();
		if (!teamStorageDir.isDirectory()) {
			teamStorageDir.mkdir();
		}

		for (String str : teamStorage.getStringList("playerLookup")) {
			String[] split = str.split(":");
			playerLookup.put(UUID.fromString(split[0]), UUID.fromString(split[1]));
		}

		for (String str : teamStorage.getStringList("teamNameLookup")) {
			String[] split = str.split(":");
			teamNameLookup.put(split[0].toLowerCase(), UUID.fromString(split[1]));
		}

		for (String str : teamStorage.getStringList("chestClaims")) {
			String[] split = str.split(";");
			if (split.length == 2) {
				chestClaims.put(split[0], UUID.fromString(split[1]));
			} else {
				Bukkit.getLogger().warning("[BetterTeams] A chest claim is found to have the details  " + str
						+ " which should be impossible. This chest claim will be ignored");
			}
		}

		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	@Override
	public boolean isTeam(UUID uuid) {
		// if the file exists for that uuid, it must be a team
		File file = new File(SeparatedYamlTeamStorage.getTeamSaveFile(), uuid.toString() + ".yml");
		return file.exists();
	}

	@Override
	public boolean isTeam(String name) {
		if(name == null) {
			return false;
		}
		return teamNameLookup.containsKey(name.toLowerCase());
	}

	@Override
	public boolean isInTeam(OfflinePlayer player) {
		return playerLookup.containsKey(player.getUniqueId());
	}

	@Override
	public UUID getTeamUUID(OfflinePlayer player) {
		return playerLookup.get(player.getUniqueId());
	}

	@Override
	public UUID getTeamUUID(String name) {
		return teamNameLookup.get(name.toLowerCase());
	}

	@Override
	public UUID getClaimingTeamUUID(Location location) {
		return chestClaims.get(LocationListComponent.getString(location));
	}

	@Override
	public void loadTeams() {

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (isInTeam(player)) {
				loadTeam(getTeamUUID(player));
			}
		}

	}

	public void loadTeam(UUID uuid) {
		if (uuid == null || isLoaded(uuid)) {
			return;
		}

		Team team;

		try {
			team = new Team(uuid);
		} catch (IllegalArgumentException e) {
			return;
		}

		loadedTeams.put(uuid, team);

	}

	public void unloadTeam(UUID uuid) {
		Team team = Team.getTeam(uuid);
		if (team.getScoreboardTeamOrNull() != null) {
			team.getScoreboardTeamOrNull().unregister();
		}
		loadedTeams.remove(uuid);
	}

	@Override
	protected void registerNewTeam(Team team, Player player) {
		addToTeamLookup(team.getName(), team.getID());

		if (player != null) {
			addToPlayerLookup(player.getUniqueId(), team.getID());
		}

	}

	public void addToTeamLookup(String name, UUID uuid) {
		teamNameLookup.put(name.toLowerCase(), uuid);
		saveTeamNameLookup();
	}

	public void addToPlayerLookup(UUID playerUUID, UUID teamUUID) {
		playerLookup.put(playerUUID, teamUUID);
		savePlayerLookup();
	}

	public void addToChestClaims(String claim, UUID teamUUID) {
		chestClaims.put(claim, teamUUID);
		saveChestClaims();
	}

	@Override
	protected void deleteTeamStorage(Team team) {
		if (team.getName() != null) {
			teamNameLookup.remove(team.getName().toLowerCase());
			saveTeamNameLookup();
		}

		File f = new File(SeparatedYamlTeamStorage.getTeamSaveFile(), team.getID() + ".yml");

		try {
			Files.delete(f.toPath());
		} catch (IOException e) {
			Bukkit.getLogger().warning("Could not delete team with the uuid " + team.getID()
					+ " there was an error while deleting the file");
			e.printStackTrace();
		}

	}

	@Override
	public void teamNameChange(Team team, String newName) {
		teamNameLookup.remove(team.getName().toLowerCase());
		teamNameLookup.put(newName.toLowerCase(), team.getID());
		saveTeamNameLookup();
	}

	@Override
	public void playerJoinTeam(Team team, TeamPlayer player) {
		addToPlayerLookup(player.getPlayer().getUniqueId(), team.getID());
	}

	@Override
	public void playerLeaveTeam(Team team, TeamPlayer player) {
		playerLookup.remove(player.getPlayer().getUniqueId());
		savePlayerLookup();
	}

	@Override
	public TeamStorage createTeamStorage(Team team) {
		return new SeparatedYamlTeamStorage(team, this);
	}

	@Override
	public TeamStorage createNewTeamStorage(Team team) {
		SeparatedYamlTeamStorage teamStorage = new SeparatedYamlTeamStorage(team, this);

		return teamStorage;
	}

	@Override
	public String[] sortTeamsByScore() {
		File folder = SeparatedYamlTeamStorage.getTeamSaveFile();
		List<IntCrossReference> teams = new ArrayList<>();

		for (File f : folder.listFiles()) {
			// team has already been resetS
			try {
				YamlConfiguration yamlConfig = new YamlConfiguration();
				yamlConfig.load(f);
				String name = yamlConfig.getString(StoredTeamValue.NAME.getReference());
				int score = yamlConfig.getInt(StoredTeamValue.SCORE.getReference());
				
				if(name == null || "".equals(name)) {
					throw new Exception();
				}
				
				teams.add(new IntCrossReference(name, score));
			} catch (Exception e) {
				Bukkit.getLogger().severe("UNABLE TO READ TEAM DATA FROM " + f);
			}
		}

		teams.sort((t1, t2) -> t2.value - t1.value);

		String[] rankedTeamsStr = new String[teams.size()];

		for (int i = 0; i < teams.size(); i++) {
			rankedTeamsStr[i] = teams.get(i).name;
			if (teams.get(i).name != null && isLoaded(getTeamUUID(teams.get(i).name))) {
				Team team = getTeam(teams.get(i).name);
				team.setTeamRank(i);
			}
		}

		return rankedTeamsStr;
	}

	@Override
	public String[] sortTeamsByBalance() {
		File folder = SeparatedYamlTeamStorage.getTeamSaveFile();
		List<DoubleCrossReference> teams = new ArrayList<>();

		for (File f : folder.listFiles()) {
			// team has already been resetS
			try {
				YamlConfiguration yamlConfig = new YamlConfiguration();
				yamlConfig.load(f);
				teams.add(new DoubleCrossReference(yamlConfig.getString(StoredTeamValue.NAME.getReference()),
						yamlConfig.getDouble(StoredTeamValue.MONEY.getReference())));

			} catch (IOException | InvalidConfigurationException e) {
				Bukkit.getLogger().severe("UNABLE TO READ TEAM DATA FROM " + f);
			}

		}

		teams.sort((t1, t2) -> {
			if (t1.value == t2.value) {
				return 0;
			} else if (t1.value < t2.value) {
				return 1;
			}
			return -1;
		});

		String[] rankedTeamsStr = new String[teams.size()];

		for (int i = 0; i < teams.size(); i++) {
			rankedTeamsStr[i] = teams.get(i).name;
			if (teams.get(i).name != null && isLoaded(getTeamUUID(teams.get(i).name))) {
				Team team = getTeam(teams.get(i).name);
				team.setTeamRank(i);
			}
		}

		return rankedTeamsStr;

	}

	@Override
	public String[] sortTeamsByMembers() {
		File folder = SeparatedYamlTeamStorage.getTeamSaveFile();
		List<IntCrossReference> teams = new ArrayList<>();
		for (File f : folder.listFiles()) {
			// team has already been resetS
			try {
				YamlConfiguration yamlConfig = new YamlConfiguration();
				yamlConfig.load(f);
				teams.add(new IntCrossReference(yamlConfig.getString(StoredTeamValue.NAME.getReference()),
						yamlConfig.getStringList("players").size()));
			} catch (Exception e) {
				Bukkit.getLogger().severe("UNABLE TO READ TEAM DATA FROM " + f);
			}
		}

		teams.sort((t1, t2) -> t2.value - t1.value);

		String[] rankedTeamsStr = new String[teams.size()];

		for (int i = 0; i < teams.size(); i++) {
			rankedTeamsStr[i] = teams.get(i).name;
			if (teams.get(i).name != null && isLoaded(getTeamUUID(teams.get(i).name))) {
				Team team = getTeam(teams.get(i).name);
				team.setTeamRank(i);
			}
		}

		return rankedTeamsStr;
	}

	@Override
	public void purgeTeamScore() {

		Map<UUID, Team> loadedTeamsClone = getLoadedTeamListClone();

		for (Entry<UUID, Team> team : loadedTeamsClone.entrySet()) {
			team.getValue().setScore(0);
		}

		// purging all teams that are not loaded async to minimise server impact
		new BukkitRunnable() {
			@Override
			public void run() {
				for (File f : teamStorageDir.listFiles()) {
					String teamID = f.getName();
					teamID = teamID.replace(".yml", "");
					// team has already been resetS
					if (loadedTeamsClone.containsKey(UUID.fromString(teamID))) {
						continue;
					}

					YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(f);
					yamlConfig.set(StoredTeamValue.SCORE.getReference(), 0);
					try {
						yamlConfig.save(f);
					} catch (IOException e) {
						Bukkit.getLogger()
								.warning("Failed to purge the score of the team with the file " + f.getPath());
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(Main.plugin);

	}

	public void savePlayerLookup() {
		List<String> toSave = new ArrayList<>();

		for (Entry<UUID, UUID> str : playerLookup.entrySet()) {
			toSave.add(str.getKey().toString() + ":" + str.getValue().toString());
		}
		teamStorage.set("playerLookup", toSave);
		saveTeamsFile();
	}

	public void saveTeamNameLookup() {
		List<String> toSave = new ArrayList<>();

		for (Entry<String, UUID> str : teamNameLookup.entrySet()) {
			toSave.add(str.getKey() + ":" + str.getValue().toString());
		}

		teamStorage.set("teamNameLookup", toSave);
		saveTeamsFile();
	}

	public void saveChestClaims() {
		List<String> toSave = new ArrayList<>();

		for (Entry<String, UUID> str : chestClaims.entrySet()) {
			toSave.add(str.getKey() + ";" + str.getValue().toString());
		}

		teamStorage.set("chestClaims", toSave);
		saveTeamsFile();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!isInTeam(e.getPlayer())) {
			return;
		}

		loadTeam(getTeamUUID(e.getPlayer()));
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (!isInTeam(e.getPlayer())) {
			return;
		}
		UUID teamUUID = getTeamUUID(e.getPlayer());

		Team team = getTeam(teamUUID);

		if (team == null) {
			// team does not exist
			return;
		}

		if (team.getOnlineMembers().size() > 1) {
			team.getTeamPlayer(e.getPlayer()).setTeamChat(false);
			return;
		}

		unloadTeam(teamUUID);

	}

	private class IntCrossReference {
		final String name;
		final int value;

		public IntCrossReference(String name, int value) {
			this.name = name;
			this.value = value;
		}
	}

	private class DoubleCrossReference {
		final String name;
		final double value;

		public DoubleCrossReference(String name, double value) {
			this.name = name;
			this.value = value;
		}
	}

	@Override
	public void addChestClaim(Team team, Location loc) {
		addToChestClaims(LocationListComponent.getString(loc), team.getID());
	}

	@Override
	public void removeChestclaim(Location loc) {
		chestClaims.remove(LocationListComponent.getString(loc));
		saveChestClaims();
	}

	@Override
	public void rebuildLookups() {
		playerLookup.clear();
		teamNameLookup.clear();
		chestClaims.clear();

		Bukkit.getLogger().info("Starting rebuilding lookup tables");

		File folder = SeparatedYamlTeamStorage.getTeamSaveFile();

		File[] files = folder.listFiles();

		int i = 0;
		for (File f : files) {
			i++;
			Bukkit.getLogger()
					.info("Rebuilding lookups for team: " + f.getName() + " (" + i + "/" + files.length + ")");
			// not the correct file type, ignore
			if (!f.getName().endsWith(".yml")) {
				continue;
			}

			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

			UUID teamUUID = UUID.fromString(f.getName().replace(".yml", ""));

			try {

				String teamName = config.getString(StoredTeamValue.NAME.getReference()).toLowerCase();

				// the file is invalid
				if (teamName == null || teamName.length() == 0) {
					continue;
				}

				teamNameLookup.put(teamName, teamUUID);

				for (String str : config.getStringList("players")) {
					UUID playerUUID = UUID.fromString(str.split(",")[0]);
					playerLookup.put(playerUUID, teamUUID);
				}

				for (String str : config.getStringList("chests")) {
					chestClaims.put(str, teamUUID);
				}
			} catch (Exception e) {
				Bukkit.getLogger()
						.warning("Could not load team `" + f.getName() + "` as file is invalid YAML, ignoring file");
			}
		}

		savePlayerLookup();
		saveTeamNameLookup();
		saveChestClaims();
	}

}
