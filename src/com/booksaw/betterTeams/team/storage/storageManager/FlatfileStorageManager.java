package com.booksaw.betterTeams.team.storage.storageManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.TeamManager;
import com.booksaw.betterTeams.team.storage.team.FlatfileTeamStorage;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class FlatfileStorageManager extends TeamManager {

	private FileConfiguration teamStorage;

	private static final String TEAMLISTSTORAGELOC = "teams";

	public FlatfileStorageManager() {
		// loading the teamStorage variable
		File f = new File("plugins/BetterTeams/teams.yml");

		if (!f.exists()) {
			Main.plugin.saveResource("teams.yml", false);
		}

		teamStorage = YamlConfiguration.loadConfiguration(f);

	}

	public void saveTeamsFile() {
		File f = new File("plugins/BetterTeams/teams.yml");
		try {
			teamStorage.save(f);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
		}
	}

	@Override
	public boolean isTeam(UUID uuid) {
		return loadedTeams.containsKey(uuid);
	}

	@Override
	public boolean isTeam(String name) {
		return getTeamUUID(name) != null;
	}

	@Override
	public boolean isInTeam(OfflinePlayer player) {

		return getTeamUUID(player) != null;

	}

	@Override
	public UUID getTeamUUID(OfflinePlayer player) {
		for (Entry<UUID, Team> team : loadedTeams.entrySet()) {
			if (team.getValue().getMembers().contains(player)) {
				return team.getKey();
			}
		}

		return null;
	}

	@Override
	public UUID getTeamUUID(String name) {
		for (Entry<UUID, Team> team : loadedTeams.entrySet()) {
			if (team.getValue().getName().equals(name)) {
				return team.getKey();
			}
		}

		return null;
	}

	@Override
	public void loadTeams() {

		for (String IDString : teamStorage.getStringList(TEAMLISTSTORAGELOC)) {
			UUID id = UUID.fromString(IDString);
			loadedTeams.put(id, new Team(id));
		}

	}

	@Override
	protected void registerNewTeam(Team team, Player owner) {
		// updating the list of teams
		List<String> teams = teamStorage.getStringList(TEAMLISTSTORAGELOC);
		teams.add(team.getID().toString());
		teamStorage.set(TEAMLISTSTORAGELOC, teams);

		saveTeamsFile();

		if (Main.plugin.teamManagement != null) {
			Main.plugin.teamManagement.displayBelowName(owner);
		}
	}

	@Override
	public void deleteTeamStorage(Team team) {
		List<String> teams = teamStorage.getStringList(TEAMLISTSTORAGELOC);
		teams.remove(team.getID().toString());
		teamStorage.set(TEAMLISTSTORAGELOC, teams);

		saveTeamsFile();

	}

	@Override
	public void teamNameChange(Team team, String newName) {
		// not needed
	}

	@Override
	public void playerJoinTeam(Team team, Player player) {
		// not needed
	}

	@Override
	public void playerLeaveTeam(Team team, Player player) {
		// not needed
	}

	@Override
	public TeamStorage createTeamStorage(Team team) {
		return new FlatfileTeamStorage(team, false, this);
	}

	@Override
	public TeamStorage createNewTeamStorage(Team team) {
		return new FlatfileTeamStorage(team, true, this);
	}

	@Override
	public String[] sortTeamsByScore() {

		Map<UUID, Team> loadedTeams = getLoadedTeamListClone();

		Team[] rankedTeams = new Team[loadedTeams.size()];

		int count = 0;
		// adding them to a list to sort
		for (Entry<UUID, Team> team : loadedTeams.entrySet()) {
			rankedTeams[count] = team.getValue();
			count++;
		}

		Arrays.sort(rankedTeams, (t1, t2) -> t2.getScore() - t1.getScore());

		String[] rankedTeamsStr = new String[loadedTeams.size()];

		for (int i = 0; i < rankedTeams.length; i++) {
			rankedTeams[i].setTeamRank(i);
			rankedTeamsStr[i] = rankedTeams[i].getName();
		}

		return rankedTeamsStr;
	}

	@Override
	public String[] sortTeamsByBalance() {
		Map<UUID, Team> loadedTeams = getLoadedTeamListClone();

		Team[] rankedTeams = new Team[loadedTeams.size()];

		int count = 0;
		// adding them to a list to sort
		for (Entry<UUID, Team> team : loadedTeams.entrySet()) {
			rankedTeams[count] = team.getValue();
			count++;
		}

		Arrays.sort(rankedTeams, (t1, t2) -> {
			if (t1.getMoney() == t2.getMoney()) {
				return 0;
			} else if (t1.getMoney() < t2.getMoney()) {
				return 1;
			}
			return -1;
		});

		String[] rankedTeamsStr = new String[loadedTeams.size()];

		for (int i = 0; i < rankedTeams.length; i++) {
			rankedTeams[i].setTeamBalRank(i);
			rankedTeamsStr[i] = rankedTeams[i].getName();
		}

		return rankedTeamsStr;
	}

	@Override
	public String[] sortTeamsByMembers() {

		Map<UUID, Team> loadedTeams = getLoadedTeamListClone();

		Team[] rankedTeams = new Team[loadedTeams.size()];

		int count = 0;

		// adding them to a list to sort
		for (Entry<UUID, Team> team : loadedTeams.entrySet()) {
			rankedTeams[count] = team.getValue();
			count++;
		}

		Arrays.sort(rankedTeams, (t1, t2) -> t2.getMembers().size() - t1.getMembers().size());

		String[] rankedTeamsStr = new String[loadedTeams.size()];

		for (int i = 0; i < rankedTeams.length; i++) {
			rankedTeamsStr[i] = rankedTeams[i].getName();
		}

		return rankedTeamsStr;
	}

	@Override
	public void purgeTeamScore() {
		for (Entry<UUID, Team> team : getLoadedTeamListClone().entrySet()) {
			team.getValue().setScore(0);
		}
	}

	private final String HOLOPATH = "holos";

	@Override
	public List<String> getHoloDetails() {
		return teamStorage.getStringList(HOLOPATH);
	}

	@Override
	public void setHoloDetails(List<String> details) {
		teamStorage.set(HOLOPATH, details);
		saveTeamsFile();
	}

	public FileConfiguration getTeamStorage() {
		return teamStorage;
	}

}
