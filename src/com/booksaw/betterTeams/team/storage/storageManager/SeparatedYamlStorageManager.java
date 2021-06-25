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
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.storage.team.SeparatedYamlTeamStorage;
import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class SeparatedYamlStorageManager extends YamlStorageManager implements Listener {

	private final Map<String, UUID> teamNameLookup = new HashMap<>();
	private final Map<UUID, UUID> playerLookup = new HashMap<>();

	private final File teamStorageDir;

	public SeparatedYamlStorageManager() {
		super();

		teamStorageDir = new File(SeparatedYamlTeamStorage.TEAMSAVEPATH);
		if (!teamStorageDir.isDirectory()) {
			teamStorageDir.mkdir();
		}

		for (String str : teamStorage.getStringList("playerLookup")) {
			String[] split = str.split(":");
			playerLookup.put(UUID.fromString(split[0]), UUID.fromString(split[1]));
		}

		for (String str : teamStorage.getStringList("teamNameLookup")) {
			String[] split = str.split(":");
			teamNameLookup.put(split[0], UUID.fromString(split[1]));
		}

		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	@Override
	public boolean isTeam(UUID uuid) {
		// if the file exists for that uuid, it must be a team
		File file = new File(SeparatedYamlTeamStorage.TEAMSAVEPATH + uuid.toString() + ".yml");
		return file.exists();
	}

	@Override
	public boolean isTeam(String name) {
		return teamNameLookup.containsKey(name);
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
		return teamNameLookup.get(name);
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

		Team team = new Team(uuid);
		loadedTeams.put(uuid, team);

	}

	public void unloadTeam(UUID uuid) {
		loadedTeams.remove(uuid);
	}

	@Override
	protected void registerNewTeam(Team team, Player player) {
		teamNameLookup.put(team.getName(), team.getID());
		saveTeamNameLookup();

		playerLookup.put(player.getUniqueId(), team.getID());
		savePlayerLookup();
	}

	@Override
	protected void deleteTeamStorage(Team team) {
		File f = new File(SeparatedYamlTeamStorage.TEAMSAVEPATH + team.getID() + ".yml");

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
		teamNameLookup.remove(team.getName());
		teamNameLookup.put(newName, team.getID());
	}

	@Override
	public void playerJoinTeam(Team team, Player player) {
		playerLookup.put(player.getUniqueId(), team.getID());
		savePlayerLookup();
	}

	@Override
	public void playerLeaveTeam(Team team, Player player) {
		playerLookup.remove(player.getUniqueId());
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
		File folder = new File(SeparatedYamlTeamStorage.TEAMSAVEPATH);
		List<IntCrossReference> teams = new ArrayList<>();

		for (File f : folder.listFiles()) {
			// team has already been resetS
			YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(f);
			teams.add(new IntCrossReference(yamlConfig.getString(StoredTeamValue.NAME.getReference()),
					yamlConfig.getInt(StoredTeamValue.SCORE.getReference())));
		}

		teams.sort((t1, t2) -> t2.value - t1.value);

		String[] rankedTeamsStr = new String[loadedTeams.size()];

		for (int i = 0; i < teams.size(); i++) {
			rankedTeamsStr[i] = teams.get(i).name;
			if (isLoaded(getTeamUUID(teams.get(i).name))) {
				Team team = getTeam(teams.get(i).name);
				team.setTeamRank(i);
			}
		}

		return rankedTeamsStr;
	}

	@Override
	public String[] sortTeamsByBalance() {
		File folder = new File(SeparatedYamlTeamStorage.TEAMSAVEPATH);
		List<DoubleCrossReference> teams = new ArrayList<>();

		for (File f : folder.listFiles()) {
			// team has already been resetS
			YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(f);
			teams.add(new DoubleCrossReference(yamlConfig.getString(StoredTeamValue.NAME.getReference()),
					yamlConfig.getDouble(StoredTeamValue.MONEY.getReference())));
		}

		teams.sort((t1, t2) -> {
			if (t1.value == t2.value) {
				return 0;
			} else if (t1.value < t2.value) {
				return 1;
			}
			return -1;
		});

		String[] rankedTeamsStr = new String[loadedTeams.size()];

		for (int i = 0; i < teams.size(); i++) {
			rankedTeamsStr[i] = teams.get(i).name;
			if (isLoaded(getTeamUUID(teams.get(i).name))) {
				Team team = getTeam(teams.get(i).name);
				team.setTeamRank(i);
			}
		}

		return rankedTeamsStr;

	}

	@Override
	public String[] sortTeamsByMembers() {
		File folder = new File(SeparatedYamlTeamStorage.TEAMSAVEPATH);
		List<IntCrossReference> teams = new ArrayList<>();

		for (File f : folder.listFiles()) {
			// team has already been resetS
			YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(f);
			teams.add(new IntCrossReference(yamlConfig.getString(StoredTeamValue.NAME.getReference()),
					yamlConfig.getStringList("players").size()));
		}

		teams.sort((t1, t2) -> t2.value - t1.value);

		String[] rankedTeamsStr = new String[loadedTeams.size()];

		for (int i = 0; i < teams.size(); i++) {
			rankedTeamsStr[i] = teams.get(i).name;
			if (isLoaded(getTeamUUID(teams.get(i).name))) {
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

		unloadTeam(getTeamUUID(e.getPlayer()));

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

}
