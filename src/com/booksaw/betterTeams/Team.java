package com.booksaw.betterTeams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import com.booksaw.betterTeams.customEvents.DisbandTeamEvent;
import com.booksaw.betterTeams.exceptions.CancelledEventException;
import com.booksaw.betterTeams.message.Message;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.message.StaticMessage;
import com.booksaw.betterTeams.team.AllyListComponent;
import com.booksaw.betterTeams.team.BanListComponent;
import com.booksaw.betterTeams.team.ChestClaimComponent;
import com.booksaw.betterTeams.team.EChestComponent;
import com.booksaw.betterTeams.team.LocationListComponent;
import com.booksaw.betterTeams.team.MemberListComponent;
import com.booksaw.betterTeams.team.MoneyComponent;
import com.booksaw.betterTeams.team.ScoreComponent;
import com.booksaw.betterTeams.team.TeamManager;

/**
 * This class is used to manage a team and all of it's participants
 *
 * @author booksaw
 */
public class Team {

	private static final TeamManager TEAMMANAGER = new TeamManager();

	public static TeamManager getTeamManager() {
		return TEAMMANAGER;
	}

	@Nullable
	public static Team getTeam(@NotNull UUID uuid) {
		return TEAMMANAGER.getTeam(uuid);
	}

	@Nullable
	public static Team getTeam(@NotNull String name) {
		return TEAMMANAGER.getTeam(name);
	}

	@Nullable
	public static Team getTeam(@NotNull OfflinePlayer player) {
		return TEAMMANAGER.getTeam(player);
	}

	@Nullable
	public static Team getTeamByName(@NotNull String name) {
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
		return TEAMMANAGER.getClaimingLocation(block);
	}

	/**
	 * @see Team#getClaimingTeam(Location) Used to get the team which has claimed
	 *      the provided chest, will return null if that location is not claimed
	 *
	 * @param location the location of the chest - must already be normalised
	 * @return The team which has claimed that chest
	 */
	@Deprecated
	public static Team getClamingTeam(Location location) {
		return getClaimingTeam(location);
	}

	public static Map<UUID, Team> getTeamList() {
		return TEAMMANAGER.getTeamListClone();
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
	public static boolean isValidTeamName(String name) {
		for (String temp : Main.plugin.getConfig().getStringList("blacklist")) {
			if (temp.equalsIgnoreCase(name.toLowerCase())) {
				return false;
			}
		}

		for (char temp : Main.plugin.getConfig().getString("bannedChars").toCharArray()) {
			if (name.contains(Character.toString(temp))) {
				return false;
			}
		}

		if (!(name.equals(ChatColor.stripColor(name)))) {
			return false;
		}

		// stop players inputting color codes
		if (name.contains("&")) {
			return false;
		}

		String allowed = Main.plugin.getConfig().getString("allowedChars");

		if (allowed.length() != 0) {
			for (char temp : name.toCharArray()) {
				if (!allowed.contains(Character.toString(temp))) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * The ID of the team (this is a unique identifier of the team which will never
	 * change)
	 */
	private UUID id;

	/**
	 * The name of the team, this can be changed after the creation of a teams, so
	 * do not store references to it
	 */
	private String name;

	/**
	 * The description of a team, used in /team info
	 */
	private String description;

	/**
	 * If the team is open or invite only
	 */
	private boolean open;

	/**
	 * The location of the teams home (/team home)
	 */
	private Location teamHome = null;

	/**
	 * tracks and provides utility methods relating to the members of this team
	 */
	private MemberListComponent members;

	/**
	 * Used to track the allies of this team
	 */
	private final AllyListComponent allies;

	/**
	 * This is a list of invited players to this team since the last restart of the
	 * server
	 */
	private List<UUID> invitedPlayers = new ArrayList<>();

	/**
	 * This is used to store all players which are banned from the team
	 */
	private final BanListComponent bannedPlayers;

	/**
	 * Used to track the chests claimed by this team
	 */
	private final ChestClaimComponent claims;

	/**
	 * The score for the team
	 */
	private final ScoreComponent score;

	/**
	 * The money that the team has
	 */
	private final MoneyComponent money;

	/**
	 * Tracks if the team has pvp enabled between team members
	 */
	private boolean pvp = false;

	private ChatColor color;
	/**
	 * the rank of the team
	 */
	private int rank;

	/**
	 * The rank on baltop of the team
	 */
	private int balRank;

	/**
	 * Used to track which teams have requested to be allies with this team
	 */
	private List<UUID> requests;

	private final EChestComponent echest;

	private int level;

	private String tag;

	HashMap<String, Warp> warps;

	private org.bukkit.scoreboard.Team team;

	/**
	 * this is used to load a team from the configuration file
	 * 
	 * @param id the ID of the team to load
	 */
	public Team(UUID id) {
		this.id = id;
		ConfigurationSection config = getConfig();

		name = config.getString("name");
		description = config.getString("description");
		open = config.getBoolean("open");
		String colorStr = config.getString("color", "6");
		color = ChatColor.getByChar(colorStr.charAt(0));

		members = new MemberListComponent();
		members.load(config);

		allies = new AllyListComponent();
		allies.load(config);

		score = new ScoreComponent();
		money = new MoneyComponent();
		echest = new EChestComponent();

		bannedPlayers = new BanListComponent();
		bannedPlayers.load(config);

		String teamHomeStr = config.getString("home");
		if (teamHomeStr != null && !teamHomeStr.equals("")) {
			teamHome = LocationListComponent.getLocation(teamHomeStr);
		}

		requests = new ArrayList<>();
		for (String string : config.getStringList("allyrequests")) {
			requests.add(UUID.fromString(string));
		}

		warps = new HashMap<>();
		for (String str : config.getStringList("warps")) {
			String[] split = str.split(";");
			warps.put(split[0], new Warp(split));
		}

		claims = new ChestClaimComponent();
		claims.load(getConfig());

		level = config.getInt("level");
		if (level < 1) {
			level = 1;
		}

		tag = config.getString("tag");

		rank = -1;

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
		ConfigurationSection config = getConfig();

		config.set("name", name);
		config.set("description", "");
		this.name = name;
		this.description = "";
		config.set("open", false);
		open = false;
		config.set("home", "");
		rank = -1;
		color = ChatColor.getByChar(Main.plugin.getConfig().getString("defaultColor").charAt(0));
		config.set("color", color.getChar());

		requests = new ArrayList<>();
		config.set("allyrequests", requests);

		warps = new HashMap<>();
		config.set("warps", new ArrayList<>());

		claims = new ChestClaimComponent();
		config.set("chests", new ArrayList<>());

		allies = new AllyListComponent();

		members = new MemberListComponent();
		members.add(this, new TeamPlayer(owner, PlayerRank.OWNER));

		score = new ScoreComponent();
		money = new MoneyComponent();
		echest = new EChestComponent();

		bannedPlayers = new BanListComponent();
		savePlayers();
		level = 1;
		config.set("level", 1);
		tag = "";
		config.set("tag", "");
		/*
		 * do not need to save config as createNewTeam saves the config after more
		 * settings modified
		 */
	}

	/**
	 * Used to get the current name of the team
	 *
	 * @return the name of the team
	 */
	public String getName() {
		return name;
	}

	/**
	 * This is used to set the name of the team, it is important that you check that
	 * the name is unique before running this method
	 *
	 * @param name the new team name
	 */
	public void setName(String name) {
		this.name = name;
		getConfig().set("name", name);
		TEAMMANAGER.saveTeamsFile();

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
		if (tag == null || tag.length() == 0) {
			return getDisplayName();
		}

		if (Main.plugin.getConfig().getBoolean("colorTeamName") && color != null) {
			return color + tag;
		}

		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
		getConfig().set("tag", tag);
		getTeamManager().saveTeamsFile();

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
	 * Returns if the team is open
	 *
	 * @return [true - anyone can join the team] [false - the team is invite only]
	 */
	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
		getConfig().set("open", open);
		getTeamManager().saveTeamsFile();
	}

	/**
	 * @return the location of the team's home
	 */
	public Location getTeamHome() {
		return teamHome;
	}

	/**
	 * @return The description of the team
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Used to change the team description
	 *
	 * @param description the new team description
	 */
	public void setDescription(String description) {
		this.description = description;
		getConfig().set("description", description);
		getTeamManager().saveTeamsFile();
	}

	/**
	 * @return The color of the team
	 */
	public ChatColor getColor() {
		return color;
	}

	/**
	 * Used to change the team color
	 *
	 * @param color the new team color
	 */
	public void setColor(ChatColor color) {
		this.color = color;
		getConfig().set("color", color.getChar());
		getTeamManager().saveTeamsFile();

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

	public MemberListComponent getMembers() {
		return members;
	}

	/**
	 * Used to save the members list to the configuration file
	 *
	 * @param config the configuration file to store the members list to
	 */
	private void savePlayers() {
		ConfigurationSection config = getConfig();
		members.save(config);
		getTeamManager().saveTeamsFile();
	}

	/**
	 * Used to save the bans list to the configuration file
	 *
	 * @param config the configuration file to store the ban list to
	 */
	private void saveBans() {
		ConfigurationSection config = getConfig();
		bannedPlayers.save(config);
		getTeamManager().saveTeamsFile();
	}

	/**
	 * Used to remove the given player from the team, you must firstly be sure that
	 * the player is in this team (as it is not checked or caught in this method)
	 *
	 * @param p the player to remove from the team
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
	 */
	public boolean removePlayer(TeamPlayer p) {
		try {
			members.remove(this, p);
		} catch (CancelledEventException e) {
			return false;
		}

		savePlayers();

		if (team != null && p.getPlayer().isOnline()) {
			Main.plugin.teamManagement.remove(p.getPlayer().getPlayer());
		}

		return true;
	}

	/**
	 * Used to get the teamPlayer version of an included player
	 *
	 * @param player the player to search for
	 * @return the team player object for that player [null - player is not in the
	 *         team]
	 */
	@Nullable
	public TeamPlayer getTeamPlayer(OfflinePlayer player) {
		return members.getTeamPlayer(player);
	}

	/**
	 * Used to get all players which have the specified rank within the team
	 *
	 * @param rank the rank to search for
	 * @return a list of players which have that rank [emtpy list - no players have
	 *         that rank]
	 */
	public List<TeamPlayer> getRank(PlayerRank rank) {
		return members.getRank(rank);
	}

	/**
	 * This command is used to disband a team, BE CAREFUL, this is irreversible
	 */
	public void disband() {
//		ConfigurationSection config = getConfig();
		DisbandTeamEvent event = new DisbandTeamEvent(this);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			throw new IllegalArgumentException("Disbanding was cancelled by another plugin");
		}

		for (UUID ally : allies.getClone()) {
			Team team = Team.getTeam(ally);
			Objects.requireNonNull(team).removeAlly(getID());

		}

		for (Entry<UUID, Team> requestedTeam : getTeamManager().getTeamListClone().entrySet()) {
			if (requestedTeam.getValue().hasRequested(getID())) {
				requestedTeam.getValue().removeAllyRequest(getID());
			}
		}

		// removing it from the team list, the java GC will handle the reset
		getTeamManager().removeTeam(id);

		// updating the list of teams
		TEAMMANAGER.getTeamStorage().set(getConfigPath(), null);

		getTeamManager().saveTeamsFile();

		if (Main.plugin.teamManagement != null) {

			for (TeamPlayer p : members.getClone()) {
				if (p.getPlayer().isOnline()) {
					Main.plugin.teamManagement.remove(p.getPlayer().getPlayer());
				}
			}

			if (team != null)
				team.unregister();
			team = null;

		}
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
				if (getTeamPlayer(p) != null) {
					return;
				}
				invitedPlayers.remove(uniqueId);

				MessageManager.sendMessageF(p, "invite.expired", getName());
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
		if (promotePlayer.getRank() == PlayerRank.DEFAULT) {
			promotePlayer.setRank(PlayerRank.ADMIN);
		} else {
			promotePlayer.setRank(PlayerRank.OWNER);
		}

		savePlayers();
	}

	/**
	 * This method is used to demote a player to the next applicable rank, this
	 * method does not check the demotion is valid but instead only promotes the
	 * player, see DemoteCommand to see validation
	 *
	 * @param demotePlayer the player to be demoted
	 */
	public void demotePlayer(TeamPlayer demotePlayer) {
		if (demotePlayer.getRank() == PlayerRank.OWNER) {
			demotePlayer.setRank(PlayerRank.ADMIN);
		} else {
			demotePlayer.setRank(PlayerRank.DEFAULT);
		}

		savePlayers();
	}

	public void setTeamHome(Location teamHome) {
		this.teamHome = teamHome;
		getConfig().set("home", LocationListComponent.getString(teamHome));
		getTeamManager().saveTeamsFile();
	}

	public void deleteTeamHome() {
		teamHome = null;
		getConfig().set("home", "");
		getTeamManager().saveTeamsFile();

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

	/**
	 * Used when a player sends a message to the team chat
	 *
	 * @param sender  the player which sent the message to the team chat
	 * @param message the message to send to the team chat
	 */
	public void sendMessage(TeamPlayer sender, String message) {
		ChatColor returnTo = ChatColor.RESET;
		String toTest = MessageManager.getMessage(sender.getPlayer().getPlayer(), "chat.syntax");
		int value = toTest.indexOf("%s");
		if (value > 2) {
			for (int i = value; i >= 0; i--) {
				if (toTest.charAt(i) == '\u00A7') {
					returnTo = ChatColor.getByChar(toTest.charAt(i + 1));
					break;
				}
			}
		}

		String fMessage = String.format(MessageManager.getMessage("chat.syntax"),
				sender.getPrefix(returnTo) + Objects.requireNonNull(sender.getPlayer().getPlayer()).getDisplayName(),
				message);

		fMessage = fMessage.replace("$name$", sender.getPrefix(returnTo) + sender.getPlayer().getPlayer().getName());
		fMessage = fMessage.replace("$message$", message);

		for (TeamPlayer player : members.getClone()) {
			if (player.getPlayer().isOnline()) {
				Objects.requireNonNull(player.getPlayer().getPlayer()).sendMessage(fMessage);
			}
		}

		for (CommandSender temp : Main.plugin.chatManagement.spy) {
			if (temp instanceof Player && getTeamPlayer((Player) temp) != null) {
				continue;
			}

			MessageManager.sendMessageF(temp, "spy.team", getName(), sender.getPlayer().getPlayer().getName(), message);
		}
		if (TEAMMANAGER.isLogChat()) {
			Bukkit.getLogger().info("[BetterTeams]" + fMessage);
		}
	}

	/**
	 * Used to send a message to all of the teams allies
	 *
	 * @param sender  the player who sent the message
	 * @param message the message that the player sent
	 */
	public void sendAllyMessage(TeamPlayer sender, String message) {
		ChatColor returnTo = ChatColor.RESET;
		String toTest = MessageManager.getMessage(sender.getPlayer().getPlayer(), "chat.syntax");
		int value = toTest.indexOf("%s");
		if (value > 2) {
			for (int i = value; i >= 0; i--) {
				if (toTest.charAt(i) == '\u00A7') {
					returnTo = ChatColor.getByChar(toTest.charAt(i + 1));
					break;
				}
			}
		}

		String fMessage = String.format(MessageManager.getMessage("allychat.syntax"), getName(),
				sender.getPrefix(returnTo) + Objects.requireNonNull(sender.getPlayer().getPlayer()).getDisplayName(),
				message);

		fMessage = fMessage.replace("$name$", sender.getPrefix(returnTo) + sender.getPlayer().getPlayer().getName());
		fMessage = fMessage.replace("$message$", message);

		Message messageI = new StaticMessage(fMessage);
		members.broadcastMessage(messageI);

		for (UUID ally : allies.getClone()) {
			Team temp = Team.getTeam(ally);
			temp.getMembers().broadcastMessage(messageI);

		}

		for (CommandSender temp : Main.plugin.chatManagement.spy) {
			if (temp instanceof Player) {
				Team spyTeam = Team.getTeam((Player) temp);
				// if they are receiving the message without chat spy
				if (spyTeam == this || (spyTeam != null && isAlly(spyTeam.getID()))) {
					continue;
				}
			}
			MessageManager.sendMessageF(temp, "spy.ally", getName(), sender.getPlayer().getName(), message);
		}

		if (TEAMMANAGER.isLogChat()) {
			Bukkit.getLogger().info("[BetterTeams]" + fMessage);
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
		this.score.save(getConfig());
		getTeamManager().saveTeamsFile();
	}

	public double getMoney() {
		return money.get();
	}

	public MoneyComponent getMoneyComponent() {
		return money;
	}

	public void setMoney(double money) {
		this.money.set(money);
		this.money.save(getConfig());
		getTeamManager().saveTeamsFile();
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

		String name = String.format(color + MessageManager.getMessage("nametag.syntax"), getTag());
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
			Bukkit.getLogger().warning(
					"An avaliable team cannot be found, be prepared for a lot of errors. (this should never happen, and should always be reported to booksaw)");
			Bukkit.getLogger().warning("This catch is merely here to stop the server crashing");
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
		savePlayers();
	}

	/**
	 * Used to add an ally for this team
	 *
	 * @param ally the UUID of the new ally
	 */
	public void addAlly(UUID ally) {
		allies.add(this, ally);
		saveAllies();
	}

	/**
	 * Used to remove an ally from this team
	 *
	 * @param ally the ally to remove
	 */
	public void removeAlly(UUID ally) {
		allies.remove(this, ally);
		saveAllies();
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
	 * Used to add an ally request to this team
	 *
	 * @param team the team that has sent the request
	 */
	public void addAllyRequest(UUID team) {
		requests.add(team);
		saveAllyRequests();

		Team t = Team.getTeam(team);
		// notifying all online owners of the team
		Message message = new ReferencedFormatMessage("ally.request", t.getDisplayName());
		members.broadcastMessage(message);
	}

	/**
	 * Used to remove an ally request from this team
	 *
	 * @param team the team to remove the ally request for
	 */
	public void removeAllyRequest(UUID team) {
		requests.remove(team);
		saveAllyRequests();
	}

	/**
	 * Used to check if a team has sent an ally request for this team
	 *
	 * @param team the team to check for
	 * @return if they have sent an ally request
	 */
	public boolean hasRequested(UUID team) {
		return requests.contains(team);
	}

	/**
	 * @return the list of all UUIDS of teams that have sent ally requests
	 */
	public List<UUID> getRequests() {
		return requests;
	}

	/**
	 * @return the list of all UUIDS of teams that are allied with this team
	 */
	public AllyListComponent getAllies() {
		return allies;
	}

	/**
	 * Used to save the list of allies for this team
	 */
	private void saveAllies() {
		allies.save(getConfig());
		getTeamManager().saveTeamsFile();
	}

	/**
	 * Used to save the list of requests for allies for this team
	 */
	private void saveAllyRequests() {
		List<String> toSave = new ArrayList<>();

		for (UUID uuid : requests) {
			toSave.add(uuid.toString());
		}

		getConfig().set("allyrequests", toSave);
		getTeamManager().saveTeamsFile();
	}

	public UUID getID() {
		return id;
	}

	/**
	 * Used to check if a member of this team can damage the specified player
	 *
	 * @param player the player to check for
	 * @return if this team can damage that player
	 */
	public boolean canDamage(Player player, Player source) {
		Team team = Team.getTeam(player);
		if (team == null) {
			return true;
		}
		return canDamage(team, source);
	}

	/**
	 * Used to check if this team can damage members of the specified team
	 *
	 * @param team the team to test
	 * @return if players of this team can damage members of the other team
	 */
	public boolean canDamage(Team team, Player source) {
		if (team.isAlly(getID()) || team == this) {

			if (pvp && team.pvp) {
				return true;
			}

			if (Main.plugin.wgManagement != null) {
				return Main.plugin.wgManagement.canTeamPvp(source);
			}

			return false;
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
		if (team == null) {
			return true;
		}
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
		List<String> toSave = new ArrayList<>();

		for (Entry<String, Warp> temp : warps.entrySet()) {
			toSave.add(temp.getValue().toString());
		}

		getConfig().set("warps", toSave);
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
		warps.put(warp.getName(), warp);
		saveWarps();
	}

	public void delWarp(String name) {
		warps.remove(name);
		saveWarps();
	}

	public Map<String, Warp> getWarps() {
		return warps;
	}

	/**
	 * Used to get a list of all the online players that are on this team
	 *
	 * @return a list of online members for this team
	 */
	public List<Player> getOnlineMemebers() {
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
		claims.save(getConfig());
		getTeamManager().saveTeamsFile();
	}

	public void saveEchest() {
		echest.save(getConfig());
		getTeamManager().saveTeamsFile();

	}

	public Inventory getEchest() {
		return echest.get();
	}

	public EChestComponent getEchestComponent() {
		return echest;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		getConfig().set("level", level);

	}

	public List<UUID> getInvitedPlayers() {
		return invitedPlayers;
	}

	public ConfigurationSection getConfig() {
		ConfigurationSection section = getTeamManager().getTeamStorage().getConfigurationSection(getConfigPath());
		if (section == null) {
			section = getTeamManager().getTeamStorage().createSection(getConfigPath());
		}
		return section;
	}

	private String getConfigPath() {
		return "team." + id;
	}

	public boolean isPvp() {
		return pvp;
	}

	public void setPvp(boolean pvp) {
		this.pvp = pvp;
	}

}
