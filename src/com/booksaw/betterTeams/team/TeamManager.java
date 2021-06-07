package com.booksaw.betterTeams.team;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.PrePurgeEvent;
import com.booksaw.betterTeams.events.ChestManagement;

public class TeamManager {
	/**
	 * A list of all teams
	 */
	private final HashMap<UUID, Team> teamList;

	/**
	 * This is used to store the config file in which the the teams data is stored
	 */
	private final FileConfiguration teamStorage;

	private final boolean logChat;

	/**
	 * Used to create a new teamManager
	 */
	public TeamManager() {
		logChat = Main.plugin.getConfig().getBoolean("logTeamChat");

		teamList = new HashMap<>();

		// loading the teamStorage variable
		File f = new File("plugins/BetterTeams/teams.yml");

		if (!f.exists()) {
			Main.plugin.saveResource("teams.yml", false);
		}

		teamStorage = YamlConfiguration.loadConfiguration(f);

	}

	/**
	 * Used to get an clone of the team list. The team objects are not cloned, just
	 * the hashmap to avoid concurrent modification
	 * 
	 * @return A clone of the team list
	 */
	@SuppressWarnings("unchecked")
	public Map<UUID, Team> getTeamListClone() {
		return (HashMap<UUID, Team>) teamList.clone();
	}

	/**
	 * Used to get the team with the provided ID
	 * 
	 * @param ID the ID of the team
	 * @return the team with that ID [null - the team does not exist]
	 */
	@Nullable
	public Team getTeam(@NotNull UUID uuid) {
		return teamList.get(uuid);
	}

	/**
	 * Used to get the team by it's display name or a player within it
	 * 
	 * @param name the display name of the team or an online player within the team
	 * @return the team which matches the data[null - no team could be found]
	 */
	@Nullable
	public Team getTeam(@NotNull String name) {

		Team team = getTeamByName(name);
		if (team != null) {
			return team;
		}

		// trying to get team by a player name
		OfflinePlayer player = Bukkit.getPlayer(name);
		if (player == null) {
			return null;
		}
		return getTeam(player);

	}

	/**
	 * Used to find the team that a specified player is in, this is the highest time
	 * complexity search to find a team (O(n^2)) so only use when the other provided
	 * methods are not possible
	 * 
	 * @param player the player which is in a team
	 * @return the team they are in [null - they are not in a team]
	 */
	@Nullable
	public Team getTeam(@NotNull OfflinePlayer player) {
		for (Entry<UUID, Team> temp : teamList.entrySet()) {
			if (temp.getValue().getMembers().contains(player)) {
				return temp.getValue();
			}
		}
		return null;
	}

	/**
	 * Used to get the team by its team name
	 * 
	 * @param name The name of the team
	 * @return The team with that display name [null - no team with that name could
	 *         be found]
	 */
	@Nullable
	public Team getTeamByName(@NotNull String name) {
		for (Entry<UUID, Team> temp : teamList.entrySet()) {
			if (name.equalsIgnoreCase(temp.getValue().getName())) {
				return temp.getValue();
			}
		}
		return null;
	}

	/**
	 * This method is used to create a new team with the specified name
	 * <p>
	 * Checks are not carried out to ensure that the name is available, so that
	 * should be done before this method is called
	 * </p>
	 * 
	 * @param name  the name of the new team
	 * @param owner the owner of the new team (the player who ran /team create)
	 */
	public void createNewTeam(String name, Player owner) {

		UUID id = UUID.randomUUID();
		// ensuring the ID is unique
		while (getTeam(id) != null) {
			id = UUID.randomUUID();
		}
		teamList.put(id, new Team(name, id, owner));
		String saveLoc = "teams";
		// updating the list of teams
		List<String> teams = teamStorage.getStringList(saveLoc);
		teams.add(id.toString());
		teamStorage.set(saveLoc, teams);

		saveTeamsFile();

		if (Main.plugin.teamManagement != null) {
			Main.plugin.teamManagement.displayBelowName(owner);
		}
	}

	/**
	 * Used to get the team which has claimed the provided chest, will return null
	 * if that location is not claimed
	 * 
	 * @param location the location of the chest - must already be normalised
	 * @return The team which has claimed that chest
	 */
	public Team getClaimingTeam(Location location) {

		for (Entry<UUID, Team> team : teamList.entrySet()) {
			if (team.getValue().isClaimed(location)) {
				return team.getValue();
			}
		}
		return null;

	}

	/**
	 * Used to get the claiming team of a chest, will check both parts of a double
	 * chest, it is assumed that the provided block is known to be a chest
	 * 
	 * @param block The block being checked
	 * @return The team which has claimed that block
	 */
	public Team getClaimingTeam(Block block) {
		// player is opening a chest
		if (block.getState() instanceof Chest) {
			Chest chest = (Chest) block.getState();
			InventoryHolder holder = chest.getInventory().getHolder();
			return getClaimingTeam(holder);
		} else if (block.getState() instanceof DoubleChest) {
			DoubleChest chest = (DoubleChest) block.getState();
			InventoryHolder holder = chest.getInventory().getHolder();
			return getClaimingTeam(holder);
		}

		return null;

	}

	/**
	 * Used to get the claiming team of a chest, will check both parts of a double
	 * chest, it is assumed that the provided block is known to be a chest
	 * 
	 * @param holder the inventory holder of the block to check
	 * @return The team which has claimed that block
	 */
	public Team getClaimingTeam(InventoryHolder holder) {
		// player is opening a chest

		if (holder instanceof DoubleChest) {
			DoubleChest doubleChest = (DoubleChest) holder;
			Team claimedBy = getClaimingTeam(ChestManagement.getLocation((Chest) doubleChest.getLeftSide()));
			if (claimedBy != null) {
				return claimedBy;
			}

			claimedBy = getClaimingTeam(ChestManagement.getLocation((Chest) doubleChest.getRightSide()));
			if (claimedBy != null) {
				return claimedBy;
			}
		} else if (holder instanceof Chest) {
			// single chest
			Team claimedBy = getClaimingTeam(ChestManagement.getLocation((Chest) holder));
			if (claimedBy != null) {
				return claimedBy;
			}
		}

		return null;
	}

	/**
	 * Used to get the claiming location, will check both parts of a double chest,
	 * it is assumed that the provided block is known to be a chest
	 * 
	 * @param block
	 * @return
	 */
	public Location getClaimingLocation(Block block) {
		// player is opening a chest
		Chest chest = (Chest) block.getState();
		InventoryHolder holder = chest.getInventory().getHolder();

		if (holder instanceof DoubleChest) {
			DoubleChest doubleChest = (DoubleChest) holder;
			Location loc = ChestManagement.getLocation((Chest) doubleChest.getLeftSide());
			Team claimedBy = getClaimingTeam(loc);
			if (claimedBy != null) {
				return loc;
			}

			loc = ChestManagement.getLocation((Chest) doubleChest.getRightSide());
			claimedBy = getClaimingTeam(ChestManagement.getLocation((Chest) doubleChest.getRightSide()));
			if (claimedBy != null) {
				return loc;
			}
		} else {
			// single chest
			Team claimedBy = getClaimingTeam(block.getLocation());
			if (claimedBy != null) {
				return block.getLocation();
			}
		}

		return null;
	}

	/**
	 * This method is used to sort all the teams into an array ranking from highest
	 * score to lowest
	 * 
	 * @return the array of teams in order of their rank
	 */
	public Team[] sortTeamsByScore() {
		Team[] rankedTeams = new Team[teamList.size()];

		int count = 0;
		// adding them to a list to sort
		for (Entry<UUID, Team> team : teamList.entrySet()) {
			rankedTeams[count] = team.getValue();
			count++;
		}

		Arrays.sort(rankedTeams, (t1, t2) -> t2.getScore() - t1.getScore());

		for (int i = 0; i < rankedTeams.length; i++) {
			rankedTeams[i].setTeamRank(i);
		}
		return rankedTeams;
	}

	public Team[] sortTeamsByBalance() {
		Team[] rankedTeams = new Team[teamList.size()];

		int count = 0;
		// adding them to a list to sort
		for (Entry<UUID, Team> team : teamList.entrySet()) {
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

		for (int i = 0; i < rankedTeams.length; i++) {
			rankedTeams[i].setTeamBalRank(i);
		}
		return rankedTeams;
	}

	/**
	 * Used to reset all team scores to 0
	 * 
	 * @return if the purge was successful (false - PrePurgeEvent was cancelled)
	 */
	public boolean purgeTeams() {
		// calling custom bukkit event
		PrePurgeEvent event = new PrePurgeEvent();
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}

		for (Entry<UUID, Team> temp : getTeamListClone().entrySet()) {
			temp.getValue().setScore(0);
		}
		Bukkit.getLogger().info("purging team score");
		return true;
	}

	/**
	 * Used to add a new team to the team list
	 * 
	 * @param team The team to add to the team list
	 */
	public void addTeam(Team team) {
		teamList.put(team.getID(), team);
	}

	/**
	 * Used to remove a team from the team list
	 * 
	 * @param id The id of the team to remove
	 */
	public void removeTeam(UUID id) {
		teamList.remove(id);
	}

	public void saveTeamsFile() {
		File f = new File("plugins/BetterTeams/teams.yml");
		try {
			teamStorage.save(f);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
		}
	}

	/**
	 * The FileConfiguration that is used to store team information
	 */
	public FileConfiguration getTeamStorage() {
		return teamStorage;
	}

	/**
	 * Loads the stored team information into the teamManager
	 */
	public void loadTeams() {

		for (String IDString : teamStorage.getStringList("teams")) {
			UUID id = UUID.fromString(IDString);
			teamList.put(id, new Team(id));
		}
	}

	public boolean isLogChat() {
		return logChat;
	}

}
