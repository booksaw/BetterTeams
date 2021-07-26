package com.booksaw.betterTeams.team.storage.storageManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.team.storage.team.FlatfileTeamStorage;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class FlatfileStorageManager extends YamlStorageManager {

	public FlatfileStorageManager() {
		super();
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
	public Team getClaimingTeam(Location location) {
		for (Entry<UUID, Team> team : getLoadedTeamListClone().entrySet()) {
			if (team.getValue().isClaimed(location)) {
				return team.getValue();
			}
		}
		return null;
	}

	@Override
	public UUID getClaimingTeamUUID(Location location) {
		for (Entry<UUID, Team> team : getLoadedTeamListClone().entrySet()) {
			if (team.getValue().isClaimed(location)) {
				return team.getValue().getID();
			}
		}
		return null;
	}

	@Override
	public void loadTeams() {

		for (String IDString : teamStorage.getStringList(TEAMLISTSTORAGELOC)) {
			UUID id = UUID.fromString(IDString);
			try {
				loadedTeams.put(id, new Team(id));
			} catch (IllegalArgumentException e) {
				// error thrown if team is invalid, the error handles itself
			}
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
	public void playerJoinTeam(Team team, TeamPlayer player) {
		// not needed
	}

	@Override
	public void playerLeaveTeam(Team team, TeamPlayer player) {
		// not needed
	}

	@Override
	public TeamStorage createTeamStorage(Team team) {
		return new FlatfileTeamStorage(team, this);
	}

	@Override
	public TeamStorage createNewTeamStorage(Team team) {
		return createTeamStorage(team);
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

	@Override
	public void addChestClaim(Team team, Location loc) {
		// not needed
	}

	@Override
	public void removeChestclaim(Location loc) {
		// not needed
	}

	@Override
	public void rebuildLookups() {
		// not needed 
	}

}
