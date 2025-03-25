package com.booksaw.betterTeams;

import com.booksaw.betterTeams.customEvents.*;
import com.booksaw.betterTeams.customEvents.post.*;
import com.booksaw.betterTeams.exceptions.CancelledEventException;
import com.booksaw.betterTeams.message.Message;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.message.StaticMessage;
import com.booksaw.betterTeams.team.*;
import com.booksaw.betterTeams.team.AnchoredPlayerUuidSetComponent.AnchorResult;
import com.booksaw.betterTeams.team.storage.StorageType;
import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to manage a team and all of it's participants
 *
 * @author booksaw
 */
public class Team {

	private static TeamManager TEAMMANAGER;

	public static void setupTeamManager(StorageType storageType) {
		if (TEAMMANAGER != null) {
			throw new IllegalArgumentException("The team manager has already been setup");
		}

		TEAMMANAGER = storageType.getNewTeamManager();

		if (Main.plugin.getConfig().getBoolean("rebuildLookups")) {

			TEAMMANAGER.rebuildLookups();

			Main.plugin.getConfig().set("rebuildLookups", false);
			Main.plugin.saveConfig();
		}

	}

	/**
	 * Used to disable betterteams so the singleton is removed
	 */
	public static void disable() {
		TEAMMANAGER.disable();
		TEAMMANAGER = null;
	}

	public static TeamManager getTeamManager() {
		return TEAMMANAGER;
	}

	public static Team getTeam(UUID uuid) {
		return TEAMMANAGER.getTeam(uuid);
	}

	public static Team getTeam(String name) {
		return TEAMMANAGER.getTeam(name);
	}

	public static @Nullable Team getTeam(OfflinePlayer player) {
		return TEAMMANAGER.getTeam(player);
	}

	public static Team getTeamByName(String name) {
		return TEAMMANAGER.getTeamByName(name);
	}

	public static Team getClaimingTeam(Block block) {
		return TEAMMANAGER.getClaimingTeam(block);
	}

	public static Team getClaimingTeam(InventoryHolder holder) {
		return TEAMMANAGER.getClaimingTeam(holder);
	}

	public static Team getClaimingTeam(Location location) {
		return TEAMMANAGER.getClaimingTeam(location);
	}

	public static Location getClaimingLocation(Block block) {
		if (block.getType() != Material.CHEST) {
			return null;
		}
		return TEAMMANAGER.getClaimingLocation(block);
	}

	/**
	 * @param location the location of the chest - must already be normalised
	 * @return The team which has claimed that chest
	 * @see Team#getClaimingTeam(Location) Used to get the team which has claimed
	 * the provided chest, will return null if that location is not claimed
	 */
	@Deprecated
	public static Team getClamingTeam(Location location) {
		return getClaimingTeam(location);
	}

	/**
	 * This no longer produces the expected result when the team manager is using
	 * anything but the flatfile storage method. Other methods should be used This
	 * is as not all teams are loaded at any point in time.
	 *
	 * @return A list of loaded teams
	 */
	@Deprecated
	public static Map<UUID, Team> getTeamList() {
		return TEAMMANAGER.getLoadedTeamListClone();
	}

	/**
	 * Used to get the config value checking if ally chests can be opened
	 *
	 * @return If ally chests can be opened
	 */
	public static boolean canOpenAllyChests() {
		return Main.plugin.getConfig().getBoolean("allowAllyChests");
	}

	/**
	 * Used to check if the provided team name is a valid name for a team
	 *
	 * @param name The name of the team
	 * @return If the team name is valid
	 */
	@Contract("null -> false")
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isValidTeamName(@Nullable String name) {
		if (name == null) {
			return false;
		}

		for (String temp : Main.plugin.getConfig().getStringList("blacklist")) {
			if (temp.equalsIgnoreCase(name.toLowerCase())) {
				return false;
			}
		}

		String chars = Main.plugin.getConfig().getString("bannedChars");
		if (chars != null) {
			for (char temp : chars.toCharArray()) {
				if (name.contains(Character.toString(temp))) {
					return false;
				}
			}
		}

		if (!(name.equals(ChatColor.stripColor(name)))) {
			return false;
		}

		// stop players inputting color codes
		if (name.contains("&") || name.contains(":")) {
			return false;
		}

		String allowed = Main.plugin.getConfig().getString("allowedChars");

		if (allowed != null && !allowed.isEmpty()) {
			for (char temp : name.toCharArray()) {
				if (!allowed.contains(Character.toString(temp))) {
					return false;
				}
			}
		}

		return true;
	}

	@Getter
	private final TeamStorage storage;

	/**
	 * The ID of the team (this is a unique identifier of the team which will never
	 * change)
	 */
	private final UUID id;

	/**
	 * The name of the team, this can be changed after the creation of a teams, so
	 * do not store references to it
	 */
	@Getter
	private String name;

	/**
	 * The description of a team, used in /team info
	 */
	@Getter
	private String description;

	/**
	 * If the team is open or invite only
	 *
	 * @return [true - anyone can join the team] [false - the team is invite only]
	 * @todo: change this to an enum - which is more expressive
	 */
	@Getter
	private boolean open;

	/**
	 * The location of the teams home (/team home)
	 */
	@Getter
	private Location teamHome = null;

	/**
	 * tracks and provides utility methods relating to the members of this team
	 */
	@Getter
	private final MemberSetComponent members = new MemberSetComponent();

	/**
	 * tracks and provides utility methods relating to anchored players of this team
	 */
	@Getter
	private final AnchoredPlayerUuidSetComponent anchoredPlayers = new AnchoredPlayerUuidSetComponent();
	/**
	 * the list of all UUIDS of teams that are allied with this team
	 */
	@Getter
	private final AllySetComponent allies = new AllySetComponent();

	/**
	 * This is a list of invited players to this team since the last restart of the
	 * server
	 */
	@Getter
	private final List<UUID> invitedPlayers = new ArrayList<>();

	/**
	 * This is used to store all players which are banned from the team
	 */
	private final BanSetComponent bannedPlayers = new BanSetComponent();

	/**
	 * Used to track the chests claimed by this team
	 */
	private final ChestClaimComponent claims = new ChestClaimComponent();

	/**
	 * The score for the team
	 */
	private final ScoreComponent score = new ScoreComponent();

	/**
	 * The money that the team has
	 */
	private final MoneyComponent money = new MoneyComponent();

	/**
	 * Tracks if the team has pvp enabled between team members
	 */
	@Getter
	private boolean pvp = false;

	/*
	 * Decides whether or not team home will serve as respawn location
	 */
	private boolean anchor = false;

	/**
	 * The color of the team
	 */
	@Getter
	private ChatColor color = null;
	/**
	 * the rank of the team
	 */
	private int rank = -1;

	/**
	 * The rank on baltop of the team
	 */
	private int balRank;

	/**
	 * Used to track which teams have requested to be allies with this team
	 */
	private final AllyRequestComponent allyRequests = new AllyRequestComponent();

	private final EChestComponent echest = new EChestComponent();

	@Getter
	private int level;

	private String tag;

	@Getter
	private final WarpSetComponent warps = new WarpSetComponent();

	private org.bukkit.scoreboard.Team team;

	/**
	 * this is used to load a team from the configuration file
	 *
	 * @param id the ID of the team to load
	 */
	public Team(UUID id) {
		this.id = id;

		storage = TEAMMANAGER.createTeamStorage(this);

		name = storage.getString(StoredTeamValue.NAME);

		if (name == null) {
			// removing it from the team list, the java GC will handle the reset
			getTeamManager().disbandTeam(this);

			throw new IllegalArgumentException(
					"The team that attempted loading is invalid, disbanding the team to avoid problems");
		}

		description = storage.getString(StoredTeamValue.DESCRIPTION);
		open = storage.getBoolean(StoredTeamValue.OPEN);
		pvp = storage.getBoolean(StoredTeamValue.PVP);
		anchor = storage.getBoolean(StoredTeamValue.ANCHOR);

		String colorStr = storage.getString(StoredTeamValue.COLOR);

		if (colorStr == null || colorStr.isEmpty()) {
			colorStr = "6";
		}

		color = ChatColor.getByChar(colorStr.charAt(0));

		members.load(storage);
		anchoredPlayers.load(storage);
		allies.load(storage);
		score.load(storage);
		money.load(storage);
		echest.load(storage);
		bannedPlayers.load(storage);

		String teamHomeStr = storage.getString(StoredTeamValue.HOME);
		if (teamHomeStr != null && !teamHomeStr.isEmpty()) {
			teamHome = LocationSetComponent.getLocation(teamHomeStr);
		}
		allyRequests.load(storage);
		warps.load(storage);

		try {
			claims.load(storage);
		} catch (IllegalArgumentException e) {
			Main.plugin.getLogger().severe("Invalid location stored in the file for the team with the ID " + id + ", " + e.getMessage());
		}

		level = storage.getInt(StoredTeamValue.LEVEL);
		if (level < 1) {
			level = 1;
		}

		tag = storage.getString(StoredTeamValue.TAG);
	}

	/**
	 * Creates a new team with the provided name
	 * <p>
	 * This is a private method as the creation of a new team should be done by the
	 * Team.createNewTeam(name) method
	 * </p>
	 *
	 * @param name  The selected name for the team
	 * @param id    The UUID of the team
	 * @param owner The owner of the team (whoever initiated the creation of the
	 *              team)
	 */
	public Team(String name, UUID id, Player owner) {
		this.id = id;

		if (name == null) {
			Main.plugin.getLogger()
					.warning("Provided team name was null, this should never occur. Team uuid = " + id);
			name = "invalidName";

			try {
				throw new IllegalArgumentException();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		this.name = name;

		storage = TEAMMANAGER.createNewTeamStorage(this);

		storage.set(StoredTeamValue.NAME, name);
		storage.set(StoredTeamValue.DESCRIPTION, "");

		this.description = "";

		storage.set(StoredTeamValue.OPEN, false);
		open = false;

		storage.set(StoredTeamValue.PVP, false);
		pvp = false;
		
		storage.set(StoredTeamValue.ANCHOR, false);
		anchor = false;

		storage.set(StoredTeamValue.HOME, "");
		rank = -1;
		color = ChatColor.getByChar(Main.plugin.getConfig().getString("defaultColor").charAt(0));
		storage.set(StoredTeamValue.COLOR, color.getChar());

		claims.save(storage);
		if (owner != null) {
			members.add(this, new TeamPlayer(owner, PlayerRank.OWNER));
		}

		savePlayers();
		saveAnchoredPlayers();
		level = 1;
		storage.set(StoredTeamValue.LEVEL, 1);
		tag = "";
		storage.set(StoredTeamValue.TAG, "");
		/*
		 * do not need to save config as createNewTeam saves the config after more
		 * settings modified
		 */
	}

	/**
	 * This is used to set the name of the team, it is important that you check that
	 * the name is unique before running this method
	 *
	 * @param name the new team namexg
	 */
	public void setName(String name, Player playerSource) {
		final String previousName = this.name;

		TeamNameChangeEvent event = new TeamNameChangeEvent(this, name, playerSource);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			throw new IllegalArgumentException("Renaming was cancelled by another plugin");
		}
		name = event.getNewTeamName();

		TEAMMANAGER.teamNameChange(this, name);
		this.name = name;
		getStorage().set(StoredTeamValue.NAME, name);

		registerTeamName();

		Bukkit.getPluginManager().callEvent(new PostTeamNameChangeEvent(this, previousName, name, playerSource));
	}

	private void registerTeamName() {
		if (Main.plugin.teamManagement != null) {
			if (team != null) {
				for (TeamPlayer p : members.getClone()) {
					if (p.getPlayer().isOnline()) {
						team.removeEntry(Objects.requireNonNull(p.getPlayer().getName()));
					}
				}
				team.unregister();
			}

			team = null;

			for (TeamPlayer p : members.getClone()) {
				if (p.getPlayer().isOnline()) {
					Main.plugin.teamManagement.displayBelowName(Objects.requireNonNull(p.getPlayer().getPlayer()));
				}
			}
		}
	}

	/**
	 * Used to get the current name of the team
	 *
	 * @param resetTo the color to return to at the end of the string
	 * @return the name of the team
	 */
	public String getDisplayName(ChatColor resetTo) {
		if (resetTo == null) {
			return getName();
		}
		if (Main.plugin.getConfig().getBoolean("colorTeamName") && color != null) {
			return color + name + resetTo;
		}
		return name;
	}

	public String getDisplayName() {
		if (Main.plugin.getConfig().getBoolean("colorTeamName") && color != null) {
			return color + name;
		}
		return name;
	}

	public String getTag() {
		if (tag == null || tag.isEmpty()) {
			return getDisplayName();
		}

		if (Main.plugin.getConfig().getBoolean("colorTeamName") && color != null) {
			return color + tag;
		}

		return tag;
	}

	public void setTag(String tag) {
		final String oldTag = getTag();

		TeamTagChangeEvent event = new TeamTagChangeEvent(this, tag);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			throw new IllegalArgumentException("Changing tag was cancelled by another plugin");
		}
		tag = event.getNewTeamTag();

		this.tag = tag;
		getStorage().set(StoredTeamValue.TAG, tag);

		registerTeamName();

		Bukkit.getPluginManager().callEvent(new PostTeamTagChangeEvent(this, oldTag, getTag()));
	}

	public void setOpen(boolean open) {
		this.open = open;
		getStorage().set(StoredTeamValue.OPEN, open);
	}

	/**
	 * Used to change the team description
	 *
	 * @param description the new team description
	 */
	public void setDescription(String description) {
		this.description = description;
		getStorage().set(StoredTeamValue.DESCRIPTION, description);
	}

	/**
	 * Used to change the team color
	 *
	 * @param color the new team color
	 */
	public void setColor(ChatColor color) {
		TeamColorChangeEvent event = new TeamColorChangeEvent(this, color);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			throw new IllegalArgumentException("Recoloring was cancelled by another plugin");
		}

		color = event.getNewTeamColor();

		final ChatColor oldColor = getColor();
		this.color = color;
		getStorage().set(StoredTeamValue.COLOR, color.getChar());

		registerTeamName();

		Bukkit.getPluginManager().callEvent(new PostTeamColorChangeEvent(this, oldColor, color));
	}

	/**
	 * Used to save the members list to the configuration file
	 */
	private void savePlayers() {
		members.save(getStorage());
	}

	/**
	 * Used to save anchored players in this team
	 */
	private void saveAnchoredPlayers() {
		anchoredPlayers.save(getStorage());
	}

	/**
	 * Used to save the bans list to the configuration file
	 */
	private void saveBans() {
		bannedPlayers.save(getStorage());
	}

	/**
	 * Used to remove the given player from the team, you must firstly be sure that
	 * the player is in this team (as it is not checked or caught in this method)
	 *
	 * @param p the player to remove from the team
	 * @return If the player was removed from the team
	 */
	public boolean removePlayer(OfflinePlayer p) {
		return removePlayer(getTeamPlayer(p));
	}

	/**
	 * Used to remove the given teamPlayer from the team, you must firstly be sure
	 * that the player is in this team (as it is not checked or caught in this
	 * method)
	 *
	 * @param p the player to remove from the team
	 * @return If the player was removed from the team
	 */
	public boolean removePlayer(TeamPlayer p) {
		try {
			members.remove(this, p);
		} catch (CancelledEventException e) {
			return false;
		}

		savePlayers();
		if(p.isAnchored()) {
			anchoredPlayers.remove(this, p.getPlayerUUID());
			saveAnchoredPlayers();
		}

		if (team != null && p.getPlayer().isOnline()) {
			Main.plugin.teamManagement.remove(p.getPlayer().getPlayer());
		}

		return true;
	}

	public boolean isPlayerAnchored(OfflinePlayer p) {
		return isPlayerAnchored(getTeamPlayer(p));
	}
	
	/**
	 * Used to check if the given team player is anchored within this team
	 * @param p the team player
	 */
	public boolean isPlayerAnchored(TeamPlayer p) {
		return anchoredPlayers.getClone().contains(p.getPlayerUUID());
	}

	public AnchorResult setPlayerAnchor(OfflinePlayer p, boolean anchor) {
		return setPlayerAnchor(getTeamPlayer(p), anchor);
	}
	
	public AnchorResult setPlayerAnchor(TeamPlayer p, boolean anchor) {
		return anchor ? anchorPlayer(p) : unanchorPlayer(p);
	}

	/**
	 * Used for anchoring this player.
	 * @param p the team player to anchor
	 * @return AnchorResult
	 */
	public AnchorResult anchorPlayer(TeamPlayer p) {
		AnchorResult result = anchoredPlayers.add(this, p);
		if(!(result == AnchorResult.SUCCESS)){
			return result;
		}
		saveAnchoredPlayers();
		return result;
	}

	/**
	 * Used for unanchoring this player.
	 * @param p the team player to unanchor
	 * @return AnchorResult
	 */
	public AnchorResult unanchorPlayer(TeamPlayer p) {
		AnchorResult result = anchoredPlayers.remove(this, p);
		if(!(result == AnchorResult.SUCCESS)){
			return result;
		}
		saveAnchoredPlayers();
		return result;
	}

	/**
	 * Used to get the teamPlayer version of an included player
	 *
	 * @param player the player to search for
	 * @return the team player object for that player [null - player is not in the
	 * team]
	 */
	@Nullable
	public TeamPlayer getTeamPlayer(OfflinePlayer player) {
		if (player == null) {
			return null;
		}
		return members.getTeamPlayer(player);
	}

	/**
	 * Used to get all players which have the specified rank within the team
	 *
	 * @param rank the rank to search for
	 * @return a list of players which have that rank [emtpy list - no players have
	 * that rank]
	 */
	public List<TeamPlayer> getRank(PlayerRank rank) {
		return members.getRank(rank);
	}

	/**
	 * This command is used to disband a team, BE CAREFUL, this is irreversible
	 */
	public void disband() {
		disband(null);
	}

	/**
	 * This command is used to disband a team, BE CAREFUL, this is irreversible
	 *
	 * @param player The player responsible for disbandment [null - initiated by console]
	 */
	public void disband(Player player) {
		DisbandTeamEvent event = new DisbandTeamEvent(this, player);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			throw new IllegalArgumentException("Disbanding was cancelled by another plugin");
		}

		// I've got to store this here, because otherwise the team information is gone.
		final Set<UUID> alliesClone = allies.getClone();
		final Set<TeamPlayer> membersClone = members.getClone();

		alliesClone.forEach(uuid -> {
			Team team = Team.getTeam(uuid);
			if (team != null) team.becomeNeutral(this, false);
		});

		for (TeamPlayer teamPlayer : membersClone) {
			getTeamManager().playerLeaveTeam(this, teamPlayer);
		}

		// removing it from the team list, the java GC will handle the reset
		getTeamManager().disbandTeam(this);

		if (Main.plugin.teamManagement != null) {
			for (TeamPlayer p : membersClone) {
				if (p.getPlayer().isOnline()) {
					Main.plugin.teamManagement.remove(p.getPlayer().getPlayer());
				}
			}

			if (team != null)
				team.unregister();
			team = null;
		}

		Bukkit.getPluginManager().callEvent(new PostDisbandTeamEvent(this, player, alliesClone, membersClone));
	}

	/**
	 * Used to check if a player is invited to this team
	 *
	 * @param uuid the UUID of the player to check
	 * @return [true - the player is invited] [false - the player is not invited]
	 */
	public boolean isInvited(UUID uuid) {
		for (UUID temp : invitedPlayers) {
			if (temp.compareTo(uuid) == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Used to create an invite for the included player to this team
	 *
	 * @param uniqueId the UUID of the player being invited
	 */
	public void invite(UUID uniqueId) {
		invitedPlayers.add(uniqueId);

		int invite = Main.plugin.getConfig().getInt("invite");

		if (invite <= 0) {
			return;
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				Player p = Bukkit.getPlayer(uniqueId);
				if (p == null || getTeamPlayer(p) != null) {
					return;
				}
				invitedPlayers.remove(uniqueId);

				MessageManager.sendMessage(p, "invite.expired", getName());
			}
		}.runTaskLaterAsynchronously(Main.plugin, invite * 20L);

	}

	/**
	 * This is used when a player is joining the team
	 *
	 * @param p the player who is joining the team
	 * @return true if the player joined the team, else false
	 */
	public boolean join(Player p) {
		try {
			members.add(this, new TeamPlayer(p, PlayerRank.DEFAULT));
		} catch (CancelledEventException e) {
			return false;
		}
		savePlayers();
		return true;

	}

	/**
	 * This method is used to promote a player to the next applicable rank, this
	 * method does not check the promotion is valid but instead only promotes the
	 * player, see PromoteCommand to see validation
	 *
	 * @param promotePlayer the player to be promoted
	 */
	public void promotePlayer(TeamPlayer promotePlayer) {
		PlayerRank oldRank = promotePlayer.getRank();
		PlayerRank newRank;
		if (promotePlayer.getRank() == PlayerRank.DEFAULT) {
			newRank = PlayerRank.ADMIN;
		} else {
			newRank = PlayerRank.OWNER;
		}


		final PromotePlayerEvent event = new PromotePlayerEvent(this, promotePlayer, oldRank, newRank);

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		promotePlayer.setRank(event.getNewRank());
		storage.promotePlayer(promotePlayer);
		savePlayers();

		Bukkit.getPluginManager().callEvent(new PostPromotePlayerEvent(this, promotePlayer, oldRank, newRank));
	}

	/**
	 * This method is used to demote a player to the next applicable rank, this
	 * method does not check the demotion is valid but instead only promotes the
	 * player, see DemoteCommand to see validation
	 *
	 * @param demotePlayer the player to be demoted
	 */
	public void demotePlayer(TeamPlayer demotePlayer) {

		PlayerRank oldRank = demotePlayer.getRank();
		PlayerRank newRank;
		if (oldRank == PlayerRank.ADMIN) {
			newRank = PlayerRank.DEFAULT;
		} else {
			newRank = PlayerRank.ADMIN;
		}
		final DemotePlayerEvent event = new DemotePlayerEvent(this, demotePlayer, oldRank, newRank);

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		demotePlayer.setRank(event.getNewRank());
		storage.demotePlayer(demotePlayer);
		savePlayers();

		Bukkit.getPluginManager().callEvent(new PostDemotePlayerEvent(this, demotePlayer, oldRank, newRank));
	}

	public void setTeamHome(Location teamHome) {
		this.teamHome = teamHome;
		getStorage().set(StoredTeamValue.HOME, LocationSetComponent.getString(teamHome));
	}

	public void deleteTeamHome() {
		teamHome = null;
		getStorage().set(StoredTeamValue.HOME, "");
		if(anchor) setAnchored(false);
	}

	/**
	 * This method is used to add a player to the list of players which are banned
	 * from the team
	 *
	 * @param player the player to add to the list
	 */
	public void banPlayer(OfflinePlayer player) {
		bannedPlayers.add(this, player.getUniqueId());
		saveBans();
	}

	/**
	 * This method is used to remove a player from the list of players which are
	 * banned from the team
	 *
	 * @param player the player to remove from the list
	 */
	public void unbanPlayer(OfflinePlayer player) {
		bannedPlayers.remove(this, player.getUniqueId());
		saveBans();
	}

	/**
	 * This method searches the ban list to check if the player is banned
	 *
	 * @param player the player to check
	 * @return [true - the player is banned] [false - the player isen't banned]
	 */
	public boolean isBanned(OfflinePlayer player) {
		return bannedPlayers.contains(player);
	}

	@Deprecated
	@Nullable <T> T getFromEvents(final T original, final T new_, final T deprecated, String warning) {
		T retVal = original;
		if (new_ != null && !retVal.equals(new_)) {
			retVal = new_;
		} else if (deprecated != null && !retVal.equals(deprecated)) {
			retVal = deprecated;
		}

		return Objects.requireNonNull(retVal, warning);
	}

	/**
	 * Used when a player sends a message to the team chat
	 *
	 * @param sender  the player which sent the message to the team chat
	 * @param message the message to send to the team chat
	 */
	public void sendMessage(TeamPlayer sender, String message) {
		String toTest = getChatSyntax(sender);
		ChatColor returnTo = getPreviousChatColor(toTest);

		// These are variables which may be modified by TeamSendMessageEvent
		Set<TeamPlayer> recipients = members.getClone();
		recipients.removeIf(teamPlayer -> !teamPlayer.getPlayer().isOnline()); // Offline players won't be recipients
		String format = getChatSyntax(sender);
		String prefix = sender.getPrefix(returnTo);

		// Notify third party plugins that a team message is going to be sent
		TeamSendMessageEvent teamSendMessageEvent = new TeamSendMessageEvent(this, sender, message, format, prefix, recipients);
		TeamPreMessageEvent deprecatedPreTeamMessageEvent = new TeamPreMessageEvent(this, sender, message, format, prefix, recipients);
		Bukkit.getPluginManager().callEvent(teamSendMessageEvent);
		Bukkit.getPluginManager().callEvent(deprecatedPreTeamMessageEvent);

		// Process any updates after the event has been dispatched
		if (teamSendMessageEvent.isCancelled() || deprecatedPreTeamMessageEvent.isCancelled()) {
			return;
		}

		message = getFromEvents(message, teamSendMessageEvent.getRawMessage(), deprecatedPreTeamMessageEvent.getRawMessage(), "Team message cannot be null");
		format = getFromEvents(format, teamSendMessageEvent.getFormat(), deprecatedPreTeamMessageEvent.getFormat(), "Team message format cannot be null");
		prefix = getFromEvents(prefix, teamSendMessageEvent.getSenderNamePrefix(), deprecatedPreTeamMessageEvent.getSenderNamePrefix(), "The prefix cannot be null");
		recipients = getFromEvents(members.getClone(), teamSendMessageEvent.getRecipients(), deprecatedPreTeamMessageEvent.getRecipients(), "Team message recipients cannot be null");

		String fMessage = MessageManager.format(format,
				prefix + Objects.requireNonNull(sender.getPlayer().getPlayer()).getDisplayName(),
				message);

		fMessage = fMessage.replace("$name$", prefix + sender.getPlayer().getPlayer().getName());
		fMessage = fMessage.replace("$message$", message);

		for (TeamPlayer player : recipients) {
			if (player.getPlayer().isOnline()) {
				Objects.requireNonNull(player.getPlayer().getPlayer()).sendMessage(fMessage);
			}
		}

		for (CommandSender temp : Main.plugin.chatManagement.spy) {
			if (temp instanceof Player && getTeamPlayer((Player) temp) != null) {
				continue;
			}

			MessageManager.sendMessage(temp, "spy.team", getName(), sender.getPlayer().getPlayer().getName(), message);
		}
		if (TEAMMANAGER.isLogChat()) {
			Main.plugin.getLogger().info(fMessage);
		}

		// Notify third party plugins that a message has been dispatched
		Bukkit.getPluginManager().callEvent(new PostTeamSendMessageEvent(this, sender, fMessage, recipients));
		Bukkit.getPluginManager().callEvent(new TeamMessageEvent(this, sender, fMessage, recipients));
	}

	private static @NotNull ChatColor getPreviousChatColor(String toTest) {
		Matcher matcher = Pattern.compile("\\{\\d+}").matcher(toTest);
		if (matcher.find()) {
			int value = matcher.start();
			if (value > 3) {
				for (int i = value; i >= 0; i--) {
					if (toTest.charAt(i) == ChatColor.COLOR_CHAR) {
						ChatColor returnTo = ChatColor.getByChar(toTest.charAt(i + 1));
						if (returnTo != null) {
							return returnTo;
						}
					}
				}
			}
		}

		return ChatColor.RESET;
	}

	/**
	 * Used to get the chat syntax and apply placeholders when possible
	 *
	 * @param sender - The team player who sent the command
	 */
	private String getChatSyntax(TeamPlayer sender) {

		if (sender != null && sender.getPlayer() != null && sender.getPlayer().isOnline() && (sender.getPlayer().getPlayer() instanceof CommandSender)) {
			return MessageManager.getMessage(sender.getPlayer().getPlayer(), "chat.syntax");
		}

		return MessageManager.getMessage("chat.syntax");
	}

	/**
	 * Used to send a message to all of the teams allies
	 *
	 * @param sender  the player who sent the message
	 * @param message the message that the player sent
	 */
	public void sendAllyMessage(TeamPlayer sender, String message) {
		String toTest = MessageManager.getMessage(sender.getPlayer().getPlayer(), "chat.syntax");
		ChatColor returnTo = getPreviousChatColor(toTest);

		String fMessage = MessageManager.getMessage("allychat.syntax", getName(),
				sender.getPrefix(returnTo) + Objects.requireNonNull(sender.getPlayer().getPlayer()).getDisplayName(),
				message);

		fMessage = fMessage.replace("$name$", sender.getPrefix(returnTo) + sender.getPlayer().getPlayer().getName());
		fMessage = fMessage.replace("$message$", message);

		Message messageI = new StaticMessage(fMessage, false);
		members.broadcastMessage(messageI);

		for (UUID ally : allies.getClone()) {
			Team temp = Team.getTeam(ally);
			temp.getMembers().broadcastMessage(messageI);

		}

		for (CommandSender temp : Main.plugin.chatManagement.spy) {
			if (temp instanceof Player) {
				Team spyTeam = Team.getTeam((Player) temp);
				// if they are receiving the message without chat spy
				if (spyTeam == this || (spyTeam != null && isAlly(spyTeam))) {
					continue;
				}
			}
			MessageManager.sendMessage(temp, "spy.ally", getName(), sender.getPlayer().getName(), message);
		}

		if (TEAMMANAGER.isLogChat()) {
			Main.plugin.getLogger().info(fMessage);
		}
	}

	public int getScore() {
		return score.get();
	}

	public ScoreComponent getScoreComponent() {
		return score;
	}

	public void setScore(int score) {
		this.score.set(score);
		this.score.save(getStorage());
	}

	public double getMoney() {
		return money.get();
	}

	public MoneyComponent getMoneyComponent() {
		return money;
	}

	public void setMoney(double money) {
		this.money.set(money);
		this.money.save(getStorage());
	}

	/**
	 * @return the rank of the team (-1 if the team has not been ranked)
	 */
	public int getTeamRank() {
		return rank;
	}

	public void setTeamRank(int rank) {
		this.rank = rank;
	}

	public void setTeamBalRank(int rank) {
		this.balRank = rank;
	}

	public int getTeamBalRank() {
		return balRank;
	}

	/**
	 * Used throughout all below name management (showing team name above player
	 * name)
	 *
	 * @param board the scoreboard to add the team to
	 * @return the team that has been created
	 */
	public org.bukkit.scoreboard.Team getScoreboardTeam(Scoreboard board) {
		if (team != null) {
			return team;
		}

		String name = color + MessageManager.getMessage("nametag.syntax", getTag());
		int attempt = 0;
		do {
			try {
				String attemptStr = ((attempt > 0) ? attempt + "" : "");
				String teamName = getName();

				while (teamName.length() + attemptStr.length() > 16) {
					teamName = teamName.substring(0, teamName.length() - 1);
				}

				if (board.getTeam(teamName + attemptStr) != null) {
					team = null;
					attempt++;
					continue;
				}
				team = board.registerNewTeam(teamName + attemptStr);

			} catch (Exception e) {
				team = null;
				attempt++;
			}

		} while (team == null && attempt < 100);

		if (team == null) {
			Main.plugin.getLogger().warning(
					"An avaliable team cannot be found, be prepared for a lot of errors. (this should never happen, and should always be reported to booksaw)");
			Main.plugin.getLogger().warning("This catch is merely here to stop the server crashing");
			return null;
		}

		Main.plugin.teamManagement.setupTeam(team, name);

		return team;

	}

	/**
	 * Used to return the scoreboard team, and not create a new one if it does not
	 * exist
	 *
	 * @return The scoreboard team (if already created)
	 */
	public org.bukkit.scoreboard.Team getScoreboardTeamOrNull() {
		return team;
	}

	public String getBalance() {
		return money.getStringFormatting();
	}

	public void setTitle(TeamPlayer player, String title) {
		player.setTitle(title);
		storage.setTitle(player);
		savePlayers();
	}

	/**
	 * call the event, let the user do stuff.
	 *
	 * @param otherTeam the other team
	 * @return true when the no status change should be effected
	 */
	private boolean callUserEvent(Team otherTeam, RelationType prevStatus, RelationType newStatus) {
		final RelationChangeTeamEvent event = new RelationChangeTeamEvent(this, otherTeam, prevStatus, newStatus);
		Bukkit.getPluginManager().callEvent(event);
		return event.isCancelled() || prevStatus == event.getNewRelation();
	}

	/**
	 * Used to add an ally for this team
	 *
	 * @param otherTeam     the UUID of the new ally
	 * @param sendPostEvent If you want the post event to be sent. This is useful if you are switching from one relation
	 *                      to another.
	 */
	public void addAlly(UUID otherTeam, boolean sendPostEvent) {
		if (isAlly(otherTeam)) return;

		RelationType prevRelation = RelationType.NEUTRAL;
		final Team other = Team.getTeam(otherTeam);
		if (callUserEvent(other, prevRelation, RelationType.ALLY)) {
			return;
		}

		allies.add(this, otherTeam);
		saveAllies();

		List<String> channelsToUse = Main.plugin.getConfig().getStringList("onAllyMessageChannel");
		final String displayName = getTeam(otherTeam).getDisplayName();
		if (channelsToUse.isEmpty() || channelsToUse.contains("CHAT")) {
			Message message = new ReferencedFormatMessage("ally.ally", displayName);
			getMembers().broadcastMessage(message);
		}
		if (channelsToUse.isEmpty() || channelsToUse.contains("TITLE")) {
			Message message = new ReferencedFormatMessage("ally.ally_title", displayName);
			getMembers().broadcastTitle(message);
		}

		if (sendPostEvent)
			Bukkit.getPluginManager().callEvent(new PostRelationChangeTeamEvent(this, other, prevRelation, RelationType.ALLY));
	}

	/**
	 * Used to add an ally for this team
	 *
	 * @param ally          the UUID of the new ally
	 * @param sendPostEvent If you want the post event to be sent. This is useful if you are switching from one relation
	 *                      to another.
	 */
	public void addAlly(@Nullable Team ally, boolean sendPostEvent) {
		if (ally == null) return;

		addAlly(ally.getID(), sendPostEvent);
	}

	public void addAlly(@Nullable Team ally) {
		addAlly(ally, true);
	}

	public void addAlly(@Nullable UUID ally) {
		addAlly(ally, true);
	}

	/**
	 * Used to remove an ally from this team
	 *
	 * @param ally the ally to remove
	 * @deprecated Use becomeNeutral
	 */
	public void removeAlly(UUID ally) {
		becomeNeutral(ally, true);
	}

	/**
	 * Used to remove an ally from this team
	 *
	 * @param ally the ally to remove
	 * @deprecated Use becomeNeutral
	 */
	@Deprecated
	public void removeAlly(@Nullable Team ally) {
		if (ally == null) return;
		becomeNeutral(ally, true);
	}

	/**
	 * Used to become neutral to a team
	 *
	 * @param otherTeam     the team to become neutral to
	 * @param sendPostEvent If you want the post event to be sent. This is useful if you are switching from one relation
	 *                      to another.
	 */
	public void becomeNeutral(UUID otherTeam, boolean sendPostEvent) {
		if (!isAlly(otherTeam)) return;

		final Team other = Team.getTeam(otherTeam);

		RelationType prevRelation = RelationType.ALLY;
		if (callUserEvent(other, prevRelation, RelationType.NEUTRAL)) return;

		allies.remove(this, otherTeam);
		saveAllies();

		List<String> channelsToUse;
		Message chatMessage, titleMessage;
		channelsToUse = Main.plugin.getConfig().getStringList("onAllyMessageChannel");
		chatMessage = new ReferencedFormatMessage("neutral.remove", other.getDisplayName());
		titleMessage = new ReferencedFormatMessage("neutral.remove_title", other.getDisplayName());

		if (channelsToUse.isEmpty() || channelsToUse.contains("CHAT")) {
			getMembers().broadcastMessage(chatMessage);
		}
		if (channelsToUse.isEmpty() || channelsToUse.contains("TITLE")) {
			getMembers().broadcastTitle(titleMessage);
		}

		if (sendPostEvent)
			Bukkit.getPluginManager().callEvent(new PostRelationChangeTeamEvent(this, other, prevRelation, RelationType.NEUTRAL));
	}

	public void becomeNeutral(Team otherTeam, boolean sendPostEvent) {
		if (otherTeam == null) return;
		becomeNeutral(otherTeam.getID(), sendPostEvent);
	}

	/**
	 * Used to check if a team is in alliance with this team
	 *
	 * @param team the team to check for allies
	 * @return if the team is an ally
	 */
	public boolean isAlly(UUID team) {
		return allies.contains(team);
	}

	/**
	 * Used to check if a team is in alliance with this team
	 *
	 * @param team the team to check for allies
	 * @return if the team is an ally
	 */
	public boolean isAlly(@Nullable Team team) {
		if (team == null) return false;

		return isAlly(team.getID());
	}

	/**
	 * Used to check if the provided team is a neutral to the other team
	 *
	 * @param team the team to check
	 * @return if the team is neutral
	 */
	public boolean isNeutral(UUID team) {
		return !allies.contains(team);
	}

	public boolean isNeutral(@Nullable Team team) {
		if (team == null) return true;

		return isNeutral(team.getID());
	}

	/**
	 * Used to add an ally request to this team
	 *
	 * @param team the team that has sent the request
	 */
	public void addAllyRequest(UUID team) {
		allyRequests.add(this, team);
		saveAllyRequests();
	}

	/**
	 * Used to add an ally request to this team
	 *
	 * @param team the team that has sent the request
	 */
	public void addAllyRequest(@Nullable Team team) {
		if (team == null) return;

		addAllyRequest(team.getID());
	}

	/**
	 * Used to remove an ally request from this team
	 *
	 * @param team the team to remove the ally request for
	 */
	public void removeAllyRequest(UUID team) {
		allyRequests.remove(this, team);
		saveAllyRequests();
	}

	/**
	 * Used to remove an ally request from this team
	 *
	 * @param team the team to remove the ally request for
	 */
	public void removeAllyRequest(@Nullable Team team) {
		if (team == null) return;

		removeAllyRequest(team.getID());
	}

	/**
	 * Used to check if a team has sent an ally request for this team
	 *
	 * @param team the team to check for
	 * @return if they have sent an ally request
	 */
	public boolean hasRequested(UUID team) {
		return allyRequests.contains(team);
	}

	/**
	 * Used to check if a team has sent an ally request for this team
	 *
	 * @param team the team to check for
	 * @return if they have sent an ally request
	 */
	public boolean hasRequested(@Nullable Team team) {
		if (team == null) return false;

		return hasRequested(team.getID());
	}

	/**
	 * @return the set of all UUIDS of teams that have sent ally requests
	 * @deprecated in favor of the more expressive getAllyRequests
	 */
	@Deprecated
	public Set<UUID> getRequests() {
		return getAllyRequests();
	}

	/**
	 * @return the set of all UUIDS of teams that have sent ally requests
	 */
	public Set<UUID> getAllyRequests() {
		return allyRequests.get();
	}

	/**
	 * Used to save the list of allies for this team
	 */
	private void saveAllies() {
		allies.save(getStorage());
	}

	/**
	 * Used to save the list of requests for allies for this team
	 */
	private void saveAllyRequests() {
		allyRequests.save(storage);
	}

	public UUID getID() {
		return id;
	}

	/**
	 * Used to check if a member of this team can damage the specified player
	 *
	 * @param player the player to check for
	 * @param source the source of the damage
	 * @return if this team can damage that player
	 */
	public boolean canDamage(Player player, Player source) {
		Team team = Team.getTeam(player);
		if (team == null) return true;
		return canDamage(team, source);
	}

	/**
	 * Used to check if this team can damage members of the specified team
	 *
	 * @param team   the team to test
	 * @param source The source of the damage
	 * @return if players of this team can damage members of the other team
	 */
	public boolean canDamage(Team team, Player source) {
		final boolean isProtected = team.isAlly(getID()) || team == this;

		boolean disallow;

		if (isProtected) {
			if (pvp && team.pvp) {
				disallow = false;
			} else if (Main.plugin.wgManagement != null) {
				disallow = !Main.plugin.wgManagement.canTeamPvp(source);
			} else
				disallow = true;

			if (disallow) {
				final TeamDisallowedPvPEvent event = new TeamDisallowedPvPEvent(team, source, this, true);

				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					return true;
			}

			return !disallow;
		}

		return true;
	}

	/**
	 * Used to check if a member of this team can damage the specified player
	 *
	 * @param player the player to check for
	 * @return if this team can damage that player
	 */
	public boolean canDamage(Player player) {
		Team team = Team.getTeam(player);
		if (team == null) return true;
		return canDamage(team);
	}

	/**
	 * Used to check if this team can damage members of the specified team
	 *
	 * @param team the team to test
	 * @return if players of this team can damage members of the other team
	 */
	public boolean canDamage(Team team) {
		if (team.isAlly(getID()) || team == this) {
			return pvp && team.pvp;
		}
		return true;
	}

	public boolean hasMaxAllies() {
		int limit = Main.plugin.getConfig().getInt("allyLimit");
		if (limit == -1) {
			return false;
		}

		return allies.size() >= limit;
	}

	/**
	 * Used to save all warps that this team has set
	 */
	public void saveWarps() {
		warps.save(storage);
	}

	/**
	 * Used to get a warp with the specified name
	 *
	 * @param name the name of the warp
	 * @return the warp with that name
	 */
	public Warp getWarp(String name) {
		return warps.get(name);
	}

	public void addWarp(Warp warp) {
		warps.add(this, warp);
		saveWarps();
	}

	public void delWarp(String name) {
		warps.remove(this, getWarp(name));
		saveWarps();
	}

	/**
	 * Used to get a list of all the online players that are on this team
	 *
	 * @return a list of online members for this team
	 */
	public List<Player> getOnlineMembers() {
		return members.getOnlinePlayers();
	}

	// CHEST CLAIM COMPONENT

	/**
	 * Used to add a chest claim to this team
	 *
	 * @param location The location of the chest claim (round to the nearest block)
	 */
	public void addClaim(Location location) {
		claims.add(this, location);
		saveClaims();
	}

	/**
	 * Used to remove a chest claim from this team
	 *
	 * @param location The location of the chest claim (round to the nearest block)
	 */
	public void removeClaim(Location location) {
		claims.remove(this, location);
		saveClaims();
	}

	public void clearClaims() {
		claims.clear();
		saveClaims();
	}

	public int getClaimCount() {
		return claims.size();
	}

	public boolean isClaimed(Location location) {
		return claims.contains(location);
	}

	private void saveClaims() {
		claims.save(getStorage());
	}

	public void saveEchest() {
		echest.save(getStorage());
	}

	public Inventory getEchest() {
		return echest.get();
	}

	public EChestComponent getEchestComponent() {
		return echest;
	}

	public int getMaxWarps() {
		return Main.plugin.getConfig().getInt("levels.l" + getLevel() + ".maxWarps");
	}

	public void setLevel(int level) {
		this.level = level;
		getStorage().set(StoredTeamValue.LEVEL, level);

	}

	public void setPvp(boolean pvp) {
		this.pvp = pvp;
		getStorage().set(StoredTeamValue.PVP, pvp);

	}

	/**
	 * Toggle anchor status for this team
	 * @return false if trying to anchor the team and its home is not set, true otherwise
	 */
	public boolean toggleAnchor() {
		return setAnchored(!anchor);
	}

	public boolean setAnchored(boolean anchor) {
		if(anchor && teamHome == null) return false;
		this.anchor = anchor;
		getStorage().set(StoredTeamValue.ANCHOR, anchor);
		return true;
	}

	public boolean isAnchored() {
		return anchor;
	}

	public double getMaxMoney() {
		return Main.plugin.getConfig().getDouble("levels.l" + getLevel() + ".maxBal");
	}

	public int getTeamLimit() {
		if (!Main.plugin.getConfig().getBoolean("permissionLevels") || Main.perms == null) {
			return Main.plugin.getConfig().getInt("levels.l" + getLevel() + ".teamLimit");
		} else {

			int limit = 1;

			// looping through every owener to find the max team limit
			for (TeamPlayer player : getRank(PlayerRank.OWNER)) {

				OfflinePlayer op = player.getPlayer();

				for (int i = 100; i > 0 && i > limit; i--) {
					if (Main.perms.playerHas(Bukkit.getWorlds().get(0).getName(), op, "betterteams.limit." + i)) {
						limit = i;
					}
				}

			}
			return limit;
		}
	}

	public boolean isTeamFull() {
		return getMembers().size() >= getTeamLimit();
	}

	public int getMaxAdmins() {
		return Main.plugin.getConfig().getInt("levels.l" + getLevel() + ".maxAdmins");
	}

	public int getMaxOwners() {
		return Main.plugin.getConfig().getInt("levels.l" + getLevel() + ".maxOwners");
	}

	public boolean isMaxAdmins() {
		int max = getMaxAdmins();
		if (max == -1) {
			return false;
		}
		return max <= getRank(PlayerRank.ADMIN).size();
	}

	public boolean isMaxOwners() {
		int max = getMaxOwners();
		if (max == -1) {
			return false;
		}
		return max <= getRank(PlayerRank.OWNER).size();

	}
}
