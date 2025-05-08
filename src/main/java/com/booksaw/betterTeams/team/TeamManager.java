package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.CreateTeamEvent;
import com.booksaw.betterTeams.customEvents.PrePurgeEvent;
import com.booksaw.betterTeams.customEvents.PurgeEvent;
import com.booksaw.betterTeams.customEvents.post.PostCreateTeamEvent;
import com.booksaw.betterTeams.customEvents.post.PostPurgeEvent;
import com.booksaw.betterTeams.events.ChestManagement;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class TeamManager {
	/**
	 * A list of all teams
	 */
	protected final HashMap<UUID, Team> loadedTeams;

	/**
	 * If chat is being logged to the console
	 */
	@Getter
	private final boolean logChat;

	/**
	 * Used to create a new teamManager
	 */
	protected TeamManager() {
		logChat = Main.plugin.getConfig().getBoolean("logTeamChat");

		loadedTeams = new HashMap<>();

	}

	/**
	 * Used to get an clone of the loaded team list. The team objects are not
	 * cloned, just the hashmap to avoid concurrent modification
	 *
	 * @return A clone of the team list
	 */
	@SuppressWarnings("unchecked")
	public Map<UUID, Team> getLoadedTeamListClone() {
		return (HashMap<UUID, Team>) loadedTeams.clone();
	}

	/**
	 * Used to get the team with the provided ID
	 *
	 * @param uuid the ID of the team
	 * @return the team with that ID [null - the team does not exist]
	 */
	@Nullable
	public Team getTeam(@NotNull UUID uuid) {

		if (loadedTeams.containsKey(uuid)) {
			return loadedTeams.get(uuid);
		}

		if (!isTeam(uuid)) {
			return null;
		}

		try {
			return new Team(uuid);
		} catch (IllegalArgumentException e) {
			return null;
		}
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

		// checking if the player is in a loaded team (save hitting secondary storage every time)
		Optional<Team> possibleTeam = loadedTeams.values().stream().filter(team -> team.getMembers().contains(player)).findFirst();
		if (possibleTeam.isPresent()) {
			return possibleTeam.get();
		}

		if (!isInTeam(player)) {
			return null;
		}

		UUID uuid = getTeamUUID(player);
		if (uuid == null) {
			return null;
		}

		return getTeam(uuid);

	}

	/**
	 * Used to get the team by its team name
	 *
	 * @param name The name of the team
	 * @return The team with that display name [null - no team with that name could
	 * be found]
	 */
	@Nullable
	public Team getTeamByName(@NotNull String name) {
		if (!isTeam(name)) {
			return null;
		}

		UUID uuid = getTeamUUID(name);

		if (uuid == null) {
			return null;
		}

		return getTeam(uuid);

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
	 * @return The created team
	 */
	public Team createNewTeam(String name, Player owner) {

		UUID id = UUID.randomUUID();
		// ensuring the ID is unique
		while (getTeam(id) != null) {
			id = UUID.randomUUID();
		}
		Team team = new Team(name, id, owner);

		CreateTeamEvent event = new CreateTeamEvent(team, owner);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			throw new IllegalArgumentException("Creating team was cancelled by another plugin");
		}

		loadedTeams.put(id, team);
		registerNewTeam(team, owner);

		if (Main.plugin.teamManagement != null && owner != null) {
			Main.plugin.teamManagement.displayBelowName(owner);
		}

		Bukkit.getPluginManager().callEvent(new PostCreateTeamEvent(team, owner));

		return team;
	}

	/**
	 * Used to get the team which has claimed the provided chest, will return null
	 * if that location is not claimed
	 *
	 * @param location the location of the chest - must already be normalised
	 * @return The team which has claimed that chest
	 */
	public Team getClaimingTeam(Location location) {
		UUID claimingTeam = getClaimingTeamUUID(location);

		if (claimingTeam == null) {
			return null;
		}

		if (!isTeam(claimingTeam)) {
			return null;
		}

		return getTeam(claimingTeam);
	}

	/**
	 * Used to get the UUID of the team which has claimed the provided chest, will
	 * return null if that location is not claimed
	 *
	 * @param location The location of the chest - must already be normalised
	 * @return the team which has claimed that chest
	 */
	public abstract UUID getClaimingTeamUUID(Location location);

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

			return getClaimingTeam(ChestManagement.getLocation((Chest) doubleChest.getRightSide()));
		} else if (holder instanceof Chest) {
			// single chest
			return getClaimingTeam(ChestManagement.getLocation((Chest) holder));
		}

		return null;
	}

	/**
	 * Used to get the claiming location, will check both parts of a double chest,
	 * it is assumed that the provided block is known to be a chest
	 *
	 * @param block Part of the chest
	 * @return The location of the claim
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
	 * Used to reset all teams scores to 0
	 *
	 * @return If the teams were purged or not
	 */
	public boolean purgeTeams(boolean money, boolean score) {
		// calling custom bukkit event
		PurgeEvent event = new PurgeEvent();
		
		@SuppressWarnings("deprecation")
		PrePurgeEvent deprecatedEvent = new PrePurgeEvent();
		Bukkit.getPluginManager().callEvent(event);
		Bukkit.getPluginManager().callEvent(deprecatedEvent);
		if (event.isCancelled() || deprecatedEvent.isCancelled()) {
			return false;
		}

		if (score) {
			Main.plugin.getLogger().info("purging team score");
			purgeTeamScore();
		}
		if (money) {
			Main.plugin.getLogger().info("purging team score");
			purgeTeamMoney();
		}

		Bukkit.getPluginManager().callEvent(new PostPurgeEvent());
		return true;
	}

	/**
	 * Used to check if a team exists with that uuid
	 *
	 * @param uuid the UUID to check
	 * @return If a team exists with that uuid
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public abstract boolean isTeam(UUID uuid);

	/**
	 * Used to check if a team exists with that name
	 *
	 * @param name the name to check
	 * @return If a team exists with that name
	 */
	public abstract boolean isTeam(String name);

	/**
	 * Used to check if the specified player is in a team
	 *
	 * @param player The player to check
	 * @return If they are in a team
	 */
	public abstract boolean isInTeam(OfflinePlayer player);

	/**
	 * Used to get the uuid of the team that the specified player is in
	 *
	 * @param player the plyaer to check for
	 * @return The team uuid
	 */
	public abstract UUID getTeamUUID(OfflinePlayer player);

	/**
	 * Used to get the team uuid from the team name
	 *
	 * @param name The name of the team
	 * @return The UUID of the specified team
	 */
	public abstract UUID getTeamUUID(String name);

	/**
	 * Used to load the stored values into the storage manager
	 */
	public abstract void loadTeams();

	public boolean isLoaded(UUID teamUUID) {
		return loadedTeams.containsKey(teamUUID);
	}

	/**
	 * Called when a new team is registered, this can be used to register it in any
	 * full team trackers The team file will be fully prepared with the members
	 * within the team
	 *
	 * @param team   The new team
	 * @param player The player that created the team
	 */
	protected abstract void registerNewTeam(Team team, Player player);

	/**
	 * Used to disband a team
	 *
	 * @param team The team that is being disbanded
	 */
	public void disbandTeam(Team team) {
		loadedTeams.remove(team.getID());

		// if a team is being disbanded due to invalid team loading, the file should not
		// be deleted to preserve data
		if (team.getName() != null) {
			deleteTeamStorage(team);
		}
	}

	/**
	 * Used when a team is disbanded, can be used to remove it from any team
	 * trackers
	 *
	 * @param team The team that is being disbanded
	 */
	protected abstract void deleteTeamStorage(Team team);

	/**
	 * Called when a team changes its name as this will effect the getTeam(String
	 * teamName) method
	 *
	 * @param team    The new team
	 * @param newName The name the team has changed to
	 */
	public abstract void teamNameChange(Team team, String newName);

	/**
	 * Called when a player joins a team, this can be used to track the players
	 * location
	 *
	 * @param team   The team that the player has joined
	 * @param player The player that has joined the team
	 */
	public abstract void playerJoinTeam(Team team, TeamPlayer player);

	/**
	 * Called when a player leaves a team
	 *
	 * @param team   The team that the player has left
	 * @param player The team that the player has left
	 */
	public abstract void playerLeaveTeam(Team team, TeamPlayer player);

	/**
	 * Called when a team needs a storage manager to manage all information, this is
	 * called for preexisting teams
	 *
	 * @param team The team instance
	 * @return The created team storage
	 */
	public abstract TeamStorage createTeamStorage(Team team);

	/**
	 * Called when a new team is made
	 *
	 * @param team The team
	 * @return The created team storage
	 */
	public abstract TeamStorage createNewTeamStorage(Team team);

	/**
	 * This method is used to sort all the teams into an array ranking from highest
	 * score to lowest
	 *
	 * @return the array of teams in order of their rank
	 */
	public abstract String[] sortTeamsByScore();

	/**
	 * This method is used to sort all the team names into an array ranking from
	 * highest to lowest
	 *
	 * @return The sorted array
	 */
	public abstract String[] sortTeamsByBalance();

	/**
	 * Used to sort all members from largest to smallest by number of members
	 *
	 * @return the sorted array
	 */
	public abstract String[] sortTeamsByMembers();

	/**
	 * Used to reset the score of all teams
	 */
	public abstract void purgeTeamScore();

	/**
	 * Used to reset the balance of all teams
	 */
	public abstract void purgeTeamMoney();

	/**
	 * @return The stored hologram details
	 */
	public abstract List<String> getHoloDetails();

	/**
	 * Used to store and save the updated hologram details
	 *
	 * @param details the details to save
	 */
	public abstract void setHoloDetails(List<String> details);

	public abstract void addChestClaim(Team team, Location loc);

	public abstract void removeChestclaim(Location loc);

	/**
	 * Can be called by a config option if the server is having difficulties. Do not
	 * call from anywhere else as it may cause problems depending on the storage
	 * type
	 */
	public abstract void rebuildLookups();

	/**
	 * this can be overritten if any code needs to be run when onDisable is called
	 */
	public void disable() {
	}
}
