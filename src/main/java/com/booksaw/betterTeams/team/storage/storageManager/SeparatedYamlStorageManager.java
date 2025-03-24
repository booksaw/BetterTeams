package com.booksaw.betterTeams.team.storage.storageManager;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.team.LocationSetComponent;
import com.booksaw.betterTeams.team.storage.team.SeparatedYamlTeamStorage;
import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;

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
				Main.plugin.getLogger().warning("A chest claim is found to have the details  " + str
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
		if (name == null) {
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
		return chestClaims.get(LocationSetComponent.getString(location));
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
			Main.plugin.getLogger().warning("Could not delete team with the uuid " + team.getID()
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
		return new SeparatedYamlTeamStorage(team, this);
	}

	@Override
	public String[] sortTeamsByScore() {
		return sortTeamByX(configuration -> configuration.getInt(StoredTeamValue.SCORE.getReference()), (t1, t2) -> t2.value - t1.value);
	}

	@Override
	public String[] sortTeamsByBalance() {
		return sortTeamByX(
				configuration -> configuration.getDouble(StoredTeamValue.MONEY.getReference()),
				(t1, t2) -> {
					if (t1.value == t2.value) {
						return 0;
					} else if (t1.value < t2.value) {
						return 1;
					}
					return -1;
				});
	}

	@Override
	public String[] sortTeamsByMembers() {
		return sortTeamByX(configuration -> configuration.getStringList("players").size(), (t1, t2) -> t2.value - t1.value);
	}

	private <T> String[] sortTeamByX(ValueSorter<T> valueSorter, Comparator<? super CrossReference<T>> comparator) {
		File folder = SeparatedYamlTeamStorage.getTeamSaveFile();
		List<CrossReference<T>> teams = new ArrayList<>();
		for (File f : folder.listFiles()) {
			// team has already been resetS
			try {
				YamlConfiguration yamlConfig = new YamlConfiguration();
				yamlConfig.load(f);
				teams.add(new CrossReference<>(yamlConfig.getString(StoredTeamValue.NAME.getReference()), valueSorter.getValueToSort(yamlConfig)));
			} catch (Exception e) {
				Main.plugin.getLogger().severe("UNABLE TO READ TEAM DATA FROM " + f);
			}
		}

		teams.sort(comparator);

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

	private static class CrossReference<T> {
		final String name;

		final T value;

		CrossReference(String name, T value) {
			this.name = name;
			this.value = value;
		}
	}

	private interface ValueSorter<T> {
		T getValueToSort(YamlConfiguration configuration);
	}

	@Override
	public void purgeTeamScore() {
		setAllTeamsValue(StoredTeamValue.SCORE, 0, team -> team.setScore(0));
	}

	@Override
	public void purgeTeamMoney() {
		setAllTeamsValue(StoredTeamValue.MONEY, 0, team -> team.setMoney(0));
	}

	private void setAllTeamsValue(StoredTeamValue storedTeamValue, Object value, ResetLoadedTeamValue function) {
		Map<UUID, Team> loadedTeamsClone = getLoadedTeamListClone();

		for (Entry<UUID, Team> team : loadedTeamsClone.entrySet()) {
			function.resetLoadedTeamValue(team.getValue());
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
					yamlConfig.set(storedTeamValue.getReference(), value);
					try {
						yamlConfig.save(f);
					} catch (IOException e) {
						Main.plugin.getLogger()
								.warning("Failed to purge the " + storedTeamValue + "of the team with the file " + f.getPath());
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(Main.plugin);
	}

	private interface ResetLoadedTeamValue {
		void resetLoadedTeamValue(Team team);
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

	@Override
	public void addChestClaim(Team team, Location loc) {
		addToChestClaims(LocationSetComponent.getString(loc), team.getID());
	}

	@Override
	public void removeChestclaim(Location loc) {
		chestClaims.remove(LocationSetComponent.getString(loc));
		saveChestClaims();
	}

	@Override
	public void rebuildLookups() {
		playerLookup.clear();
		teamNameLookup.clear();
		chestClaims.clear();

		Main.plugin.getLogger().info("Starting rebuilding lookup tables");

		File folder = SeparatedYamlTeamStorage.getTeamSaveFile();

		File[] files = folder.listFiles();

		int i = 0;
		for (File f : files) {
			i++;
			Main.plugin.getLogger()
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
				if (teamName == null || teamName.isEmpty()) {
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
				Main.plugin.getLogger()
						.warning("Could not load team `" + f.getName() + "` as file is invalid YAML, ignoring file");
			}
		}

		savePlayerLookup();
		saveTeamNameLookup();
		saveChestClaims();
	}

}
