package com.booksaw.betterTeams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.booksaw.betterTeams.commands.HelpCommand;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.PermissionParentCommand;
import com.booksaw.betterTeams.commands.team.AllyChatCommand;
import com.booksaw.betterTeams.commands.team.AllyCommand;
import com.booksaw.betterTeams.commands.team.BalCommand;
import com.booksaw.betterTeams.commands.team.BaltopCommand;
import com.booksaw.betterTeams.commands.team.BanCommand;
import com.booksaw.betterTeams.commands.team.ChatCommand;
import com.booksaw.betterTeams.commands.team.ColorCommand;
import com.booksaw.betterTeams.commands.team.CreateCommand;
import com.booksaw.betterTeams.commands.team.DelHome;
import com.booksaw.betterTeams.commands.team.DelwarpCommand;
import com.booksaw.betterTeams.commands.team.DemoteCommand;
import com.booksaw.betterTeams.commands.team.DepositCommand;
import com.booksaw.betterTeams.commands.team.DescriptionCommand;
import com.booksaw.betterTeams.commands.team.DisbandCommand;
import com.booksaw.betterTeams.commands.team.EchestCommand;
import com.booksaw.betterTeams.commands.team.HomeCommand;
import com.booksaw.betterTeams.commands.team.InfoCommand;
import com.booksaw.betterTeams.commands.team.InviteCommand;
import com.booksaw.betterTeams.commands.team.JoinCommand;
import com.booksaw.betterTeams.commands.team.KickCommand;
import com.booksaw.betterTeams.commands.team.LeaveCommand;
import com.booksaw.betterTeams.commands.team.ListCommand;
import com.booksaw.betterTeams.commands.team.NameCommand;
import com.booksaw.betterTeams.commands.team.NeutralCommand;
import com.booksaw.betterTeams.commands.team.OpenCommand;
import com.booksaw.betterTeams.commands.team.PromoteCommand;
import com.booksaw.betterTeams.commands.team.PvpCommand;
import com.booksaw.betterTeams.commands.team.RankCommand;
import com.booksaw.betterTeams.commands.team.RankupCommand;
import com.booksaw.betterTeams.commands.team.SetOwnerCommand;
import com.booksaw.betterTeams.commands.team.SetWarpCommand;
import com.booksaw.betterTeams.commands.team.SethomeCommand;
import com.booksaw.betterTeams.commands.team.TagCommand;
import com.booksaw.betterTeams.commands.team.TitleCommand;
import com.booksaw.betterTeams.commands.team.TopCommand;
import com.booksaw.betterTeams.commands.team.UnbanCommand;
import com.booksaw.betterTeams.commands.team.WarpCommand;
import com.booksaw.betterTeams.commands.team.WarpsCommand;
import com.booksaw.betterTeams.commands.team.WithdrawCommand;
import com.booksaw.betterTeams.commands.team.chest.ChestClaimCommand;
import com.booksaw.betterTeams.commands.team.chest.ChestRemoveCommand;
import com.booksaw.betterTeams.commands.team.chest.ChestRemoveallCommand;
import com.booksaw.betterTeams.commands.teama.ChatSpyTeama;
import com.booksaw.betterTeams.commands.teama.ColorTeama;
import com.booksaw.betterTeams.commands.teama.CreateHoloTeama;
import com.booksaw.betterTeams.commands.teama.CreateTeama;
import com.booksaw.betterTeams.commands.teama.DelwarpTeama;
import com.booksaw.betterTeams.commands.teama.DemoteTeama;
import com.booksaw.betterTeams.commands.teama.DescriptionTeama;
import com.booksaw.betterTeams.commands.teama.DisbandTeama;
import com.booksaw.betterTeams.commands.teama.EchestTeama;
import com.booksaw.betterTeams.commands.teama.HomeTeama;
import com.booksaw.betterTeams.commands.teama.InviteTeama;
import com.booksaw.betterTeams.commands.teama.JoinTeama;
import com.booksaw.betterTeams.commands.teama.LeaveTeama;
import com.booksaw.betterTeams.commands.teama.NameTeama;
import com.booksaw.betterTeams.commands.teama.OpenTeama;
import com.booksaw.betterTeams.commands.teama.PromoteTeama;
import com.booksaw.betterTeams.commands.teama.PurgeTeama;
import com.booksaw.betterTeams.commands.teama.ReloadTeama;
import com.booksaw.betterTeams.commands.teama.RemoveHoloTeama;
import com.booksaw.betterTeams.commands.teama.SetOwnerTeama;
import com.booksaw.betterTeams.commands.teama.SetrankTeama;
import com.booksaw.betterTeams.commands.teama.SetwarpTeama;
import com.booksaw.betterTeams.commands.teama.TagTeama;
import com.booksaw.betterTeams.commands.teama.TitleTeama;
import com.booksaw.betterTeams.commands.teama.VersionTeama;
import com.booksaw.betterTeams.commands.teama.WarpTeama;
import com.booksaw.betterTeams.commands.teama.chest.ChestClaimTeama;
import com.booksaw.betterTeams.commands.teama.chest.ChestDisableClaims;
import com.booksaw.betterTeams.commands.teama.chest.ChestEnableClaims;
import com.booksaw.betterTeams.commands.teama.chest.ChestRemoveTeama;
import com.booksaw.betterTeams.commands.teama.chest.ChestRemoveallTeama;
import com.booksaw.betterTeams.commands.teama.score.AddScore;
import com.booksaw.betterTeams.commands.teama.score.RemoveScore;
import com.booksaw.betterTeams.commands.teama.score.SetScore;
import com.booksaw.betterTeams.cooldown.CooldownManager;
import com.booksaw.betterTeams.cost.CostManager;
import com.booksaw.betterTeams.events.AllyManagement;
import com.booksaw.betterTeams.events.ChatManagement;
import com.booksaw.betterTeams.events.ChestManagement;
import com.booksaw.betterTeams.events.DamageManagement;
import com.booksaw.betterTeams.events.InventoryManagement;
import com.booksaw.betterTeams.events.MCTeamManagement;
import com.booksaw.betterTeams.events.MCTeamManagement.BelowNameType;
import com.booksaw.betterTeams.events.RankupEvents;
import com.booksaw.betterTeams.integrations.HologramManager;
import com.booksaw.betterTeams.integrations.TeamPlaceholders;
import com.booksaw.betterTeams.integrations.WorldGaurdManager;
import com.booksaw.betterTeams.integrations.ZKothManager;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.metrics.Metrics;
import com.booksaw.betterTeams.score.ScoreManagement;

import net.milkbowl.vault.economy.Economy;

/**
 * Main class of the plugin, extends JavaPlugin
 * 
 * @author booksaw
 *
 */
public class Main extends JavaPlugin {

	public static Main plugin;
	public boolean useHolographicDisplays;
	public static Economy econ = null;
	public static boolean placeholderAPI = false;

	private DamageManagement damageManagement;
	public MCTeamManagement teamManagement;
	public ChatManagement chatManagement;
	public WorldGaurdManager wgManagement;
	Metrics metrics = null;

	@Override
	public void onLoad() {
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
			wgManagement = new WorldGaurdManager();
		}
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		setupMetrics();

		plugin = this;

		MessageManager.lang = getConfig().getString("language");
		if (MessageManager.lang.equals("en") || MessageManager.lang.equals("")) {
			MessageManager.lang = "messages";
		}

		loadCustomConfigs();
		ChatManagement.enable();
		Team.loadTeams();

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
				&& Bukkit.getPluginManager().getPlugin("PlaceholderAPI").isEnabled()) {
			placeholderAPI = true;
			new TeamPlaceholders(this).register();

		}

		useHolographicDisplays = (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null
				&& Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"));

		if (useHolographicDisplays) {
			updateHolos();
		}

		if (!setupEconomy() || !getConfig().getBoolean("useVault")) {
			econ = null;
		}

		setupListeners();
		setupCommands();
	}

	@Override
	public void onDisable() {

		for (Entry<Player, Team> temp : InventoryManagement.adminViewers.entrySet()) {
			temp.getKey().closeInventory();
			temp.getValue().saveEchest();
		}

		if (useHolographicDisplays) {
			HologramManager.holoManager.disable();
		}

		if (teamManagement != null) {
			teamManagement.removeAll();
		}

	}

	/**
	 * This is used to store the config file in which the the teams data is stored
	 */
	FileConfiguration teams;

	public void loadCustomConfigs() {

		File f = MessageManager.getFile();

		try {
			if (!f.exists()) {
				saveResource(MessageManager.lang + ".yml", false);
			}
		} catch (Exception e) {
			Bukkit.getLogger().warning("Could not load selected language: " + MessageManager.lang
					+ " go to https://github.com/booksaw/BetterTeams/wiki/Language to view a list of supported languages");
			Bukkit.getLogger().warning("Reverting to english so the plugin can still function");
			MessageManager.lang = "messages";
			loadCustomConfigs();
			return;
		}

		YamlConfiguration messages = YamlConfiguration.loadConfiguration(f);

		/*
		 * this is required for every value above version 1.0 of the config (as when the
		 * user updates the plugin, it should not require the config to be refreshed
		 */
		addDefaults(messages);

		MessageManager.addMessages(messages);

		f = new File("plugins/BetterTeams/teams.yml");

		if (!f.exists()) {
			saveResource("teams.yml", false);
		}

		teams = YamlConfiguration.loadConfiguration(f);

		if (getConfig().getBoolean("disableCombat")) {
			if (damageManagement == null) {
				damageManagement = new DamageManagement();
				getServer().getPluginManager().registerEvents(damageManagement, this);
			}

		} else {
			if (damageManagement != null) {
				Bukkit.getLogger().log(Level.WARNING, "Restart server for damage changes to apply");
			}
		}

		// loading the fully custom help message option
		HelpCommand.setupHelp();

	}

	/**
	 * This method is used to add any config values which are required post 3.0
	 * 
	 * @param messages
	 */
	private void addDefaults(YamlConfiguration messages) {

		int version = messages.getInt("version");
		boolean changes = false;

		// use the case as the previous version of the config
		switch (version) {
		case 0:
			messages.set("placeholder.noTeam", "");
			messages.set("placeholder.noDescription", "");
		case 1:
			messages.set("noPerm", "&4You do not have permission to do that");
		case 2:
			messages.set("create.banned", "&4That team name is banned");
			messages.set("create.maxLength", "&4That team name is too long");
		case 3:
			messages.set("info.score", "&6Score: &b%s");
			messages.set("admin.holo.create.success", "&6Hologram created");
			messages.set("admin.holo.remove.noHolo", "&4No holograms found");
			messages.set("admin.holo.remove.success", "&6Hologram deleted");
			messages.set("holo.leaderboard", "&6Leaderboard");
			messages.set("holo.syntax", "&6%s: &b%s");
			messages.set("nametag.syntax", "&6&l%s&r ");
		case 4:
			messages.set("color.success", "&6Your team color has been changed");
			messages.set("color.fail", "&6That is not a recognised chat color");
			messages.set("info.money", "&6Balance: &b£%s");
			messages.set("deposit.tooLittle", "&4You cannot deposit negative amounts");
			messages.set("deposit.fail", "&4The deposit failed");
			messages.set("deposit.success", "&6Money deposited");
			messages.set("withdraw.tooLittle", "&4You cannot widthraw negative amounts");
			messages.set("withdraw.fail", "&4The withdrawal failed");
			messages.set("withdraw.success", "&6Money withdrawn");
			messages.set("withdraw.notEnough", "&6Your team does not have enougn money");
		case 5:
			messages.set("prefixSyntax", "&b[%s] &r%s");
		case 6:
			messages.set("spy.stop", "&6You are no longer spying on team messages");
			messages.set("spy.start", "&6You are now spying on team messages");
			messages.set("placeholder.owner", "owner");
			messages.set("placeholder.admin", "admin");
			messages.set("placeholder.default", "Default");
			messages.set("prefix.owner", " **");
			messages.set("prefix.admin", " *");
			messages.set("prefix.default", " ");
			messages.set("title.change", "&6Your title has been changed to &b%s");
			messages.set("title.success", "&6That title has been changed");
			messages.set("bannedChar", "&4A character you tried to use is banned");
			messages.set("title.tooLong", "&4That title is too long");
			messages.set("title.noFormat", "&4You do not have permission to format titles");
			messages.set("title.noColor", "&4You do not have permission to color titles");
			messages.set("title.reset", "&6The title has been reset");
			messages.set("admin.title.success", "&6That players title has been changed");
			messages.set("admin.title.reset", "&6Your title has been removed");
			messages.set("admin.inTeam", "&4That player is not in a team");
			messages.set("admin.version", "&6Current plugin version: %s");
			messages.set("top.leaderboard", "&6Leaderboard");
			messages.set("top.sytax", "&b%s: &6%s &7(%s)");
			messages.set("top.divide", "&f...");
			messages.set("rank.noTeam", "&4Team not found");
			messages.set("rank.info", "&6Team position:");
			messages.set("rank.syntax", "&b%s: &6%s &7(%s)");
			messages.set("admin.noTeam", "&4That is not a team");
			messages.set("admin.home.success", "&6You have been teleported to that teams home");
			messages.set("admin.home.noHome", "&4That team does not have a home");
			messages.set("delhome.success", "&6Your team home has been deleted");
			messages.set("delhome.noHome", "&4Your team has not set a home");
			messages.set("cooldown.wait", "&4You need to wait another %s seconds before running that!");
			messages.set("cost.tooPoor", "&4You are too poor to run that command");
			messages.set("cost.run", "&4&l-%s");
		case 7:
			messages.set("admin.name.success", "&6The team name has been changed");
			messages.set("admin.description.success", "&6That teams name has been changed");
			messages.set("admin.open.successopen", "&6That team is now open for everyone");
			messages.set("admin.open.successclose", "&6That team is now invite only");
			messages.set("admin.invite.success", "&6That player has been invited to that team");
			messages.set("holo.msyntax", "&6%s: &b$%s");
			messages.set("noTeam", "&4That team does not exist");
			messages.set("ally.already", "&4You are already allies");
			messages.set("ally.success", "&6Your teams are now allies");
			messages.set("ally.ally", "&6Your team is now allied with &b%s");
			messages.set("ally.requested", "&6An ally request has been sent to that team");
			messages.set("ally.request", "&b%s &6has sent an ally request, use &b/team ally <team> &6to accept");
			messages.set("ally.self", "&4You cannot ally your own team");
			messages.set("info.ally", "&6Allies: &b%s");
			messages.set("ally.from", "&6You have ally requests from: %s");
			messages.set("ally.noRequests", "&4You do not have any ally requests");
			messages.set("neutral.self", "&6That is your own team");
			messages.set("neutral.requestremove", "&6That ally request has been removed");
			messages.set("neutral.reject", "&4Your ally request with &b%s &4has been rejected");
			messages.set("neutral.notAlly", "&4You are not allied with that team");
			messages.set("neutral.success", "&4You are no longer allied with that team");
			messages.set("neutral.remove", "&4You are no longer allied with &b%s");
			messages.set("ally.onJoin", "&6You have new ally requests do &b/team ally &6to view them");
			messages.set("allychat.disabled", "&6Your messages are no longer going to the ally chat");
			messages.set("allychat.enabled", "&6Your messages are now going to the ally chat");
			messages.set("allychat.syntax", "&d[%s]&f%s: %s");
			messages.set("list.noPage", "&6That page is not found");
			messages.set("list.header", "&7--- &blist page %s &7---");
			messages.set("list.body", "&6%s: &b%s");
			messages.set("list.footer", "&7--- &6do &b/team list [page] &6to view more &7---");
			messages.set("teleport.fail", "&4The teleportation failed");
			messages.set("teleport.wait", "&6Wait &b%s &6seconds");
			messages.set("setowner.use",
					"&6You cannot promote that player, use &b/team setowner <player> &6to promote that player to owner");
			messages.set("setowner.success", "&6That player is now owner");
			messages.set("setowner.notify", "&6You are now owner of your team");
			messages.set("setowner.max", "&4That player is already owner");
			messages.set("admin.create.success", "&6That team has been created");
			messages.set("admin.join.banned", "&4That player is banned from that team");
			messages.set("admin.join.success", "&6that player has joined the team");
			messages.set("admin.join.notify", "&6You have joined the team &b%s");
			messages.set("admin.join.full", "&4That team is full");
			messages.set("admin.notInTeam", "&6That player cannot be in a team before doing that");
			messages.set("admin.inTeam", "&4That player is not in a team");
			messages.set("admin.leave.success", "&6That player has left that team");
			messages.set("admin.leave.notify", "&6You have left that team");
			messages.set("admin.demote.notify", "&6You have been demoted");
			messages.set("admin.demote.success", "&6That player has been demoted");
			messages.set("admin.demote.min", "&4That player cannot be demoted any more");
			messages.set("admin.promote.max", "&6That player is already the maximum rank");
			messages.set("admin.promote.notify", "&6You have been promoted");
			messages.set("admin.promote.success", "&6That player has been promoted");
			messages.set("admin.promote.owner",
					"&6It is configured that teams can only have a single owner, do &b/teama setowner <player> &6To set the player as the owner");
			messages.set("admin.setowner.already", "&4That player is already an owner");
			messages.set("admin.setowner.nonotify", "&6You are no longer owner of the team");
			messages.set("admin.setowner.success", "&6That player is the new team owner");
			messages.set("admin.setowner.notify", "&6You are now the owner of your team");
			messages.set("spy.team", "&b[%s]&f%s&f: %s");
			messages.set("spy.ally", "&d[%s]&f%s&f: %s");
		case 8:
			messages.set("top.syntax", "&b%s: &6%s &7(%s)");
			messages.set("top.divide", "&f...");
			messages.set("top.leaderboard", "&6Leaderboard");
			messages.set("neutral.success", "&4You are no longer allied with that team");
			messages.set("color.banned", "&4You can only use colors, not formatting codes");
			messages.set("setwarp.exist", "&4That warp already exists");
			messages.set("warp.nowarp", "&4That warp does not exist");
			messages.set("warp.invalidPassword", "&4Invalid password for that warp");
			messages.set("warp.success", "&6You have been teleported");
			messages.set("warps.syntax", "&6Warps: &b%s");
			messages.set("warps.none", "&4Your team has no warps set");
			messages.set("delwarp.success", "&4That warp has been deleted");
			messages.set("setwarp.success", "&6That warp has been created");
			messages.set("setwarp.max", "&4Your team already has the maximum number of warps set");
			messages.set("admin.warps.none", "&4That team has not set any warps");
			messages.set("admin.setwarp.success", "&6That warp has been set ");
			messages.set("admin.setwarp.max", "&6That team has already set the maximum number of warps");
		case 9:
			messages.set("admin.purge.confirm",
					"&6Run that command in the next 10 seconds to confirm, &4THIS CANNOT BE UNDONE AND WILL RESET SCORES FOR ALL TEAMS");
			messages.set("admin.purge.success", "&6The teams have been purged");
			messages.set("admin.score.tooSmall", "&4The score must be greater than 0");
			messages.set("admin.score.success", "&6That teams score has been changed");
		case 10:
			if (messages.getString("invite.invite") == null || messages.getString("invite.invite").equals("")) {
				messages.set("invite.success", "&6That player has been invited");
				messages.set("invite.invite",
						"&6You have been invited to join team %s do &b/team join <team> &6 to join the team");
				messages.addDefault("invite.success", "&6That player has been invited");
				messages.addDefault("invite.invite",
						"&6You have been invited to join team %s do &b/team join <team> &6 to join the team");
			}
			messages.set("admin.update",
					"&4There is a new version of better teams released update here: (https://www.spigotmc.org/resources/better-teams.17129/)");

		case 11:
			messages.set("placeholder.name", "%s");
			// messages.set("", "");
		case 12:
			messages.set("admin.disband.success", "&6That team has been disbanded successfully");
			messages.set("admin.color.success", "&6That teams color has been changed");
		case 13:
			messages.set("chest.claim.noChest", "&4You are not standing on a chest");
			messages.set("chest.claim.limit", "&4Your team has claimed the maximum number of chests");
			messages.set("chest.claim.claimed", "&4That chest has already been claimed");
			messages.set("chest.claim.success", "&6You have claimed that chest");
			messages.set("chest.remove.noChest", "&4You are not standing on a chest");
			messages.set("chest.remove.notClaimed", "&4Your team has not claimed that chest");
			messages.set("chest.remove.success", "&4Your team no longer has a claim to that chest");
			messages.set("chest.all.success", "&6Unclaimed all chests");
			messages.set("chest.claimed", "&4That chest is claimed by &a%s");
			messages.set("admin.chest.claim.success", "&6You have claimed that chest on behalf of the team");
			messages.set("admin.chest.remove.success", "&6You have successfully removed the claim from that chest");
			messages.set("admin.chest.all.success", "&6All claims removed from that team");
		case 14:
			// fixing a few typos (https://github.com/booksaw/BetterTeams/issues/31)
			if (messages.getString("neutral.success").contains("wih")) {
				messages.set("neutral.success", messages.getString("neutral.success").replace("wih", "with"));
			}
			if (messages.getString("chest.remove.success").startsWith("&4your")) {
				messages.set("chest.remove.success",
						messages.getString("chest.remove.success").replace("&4your", "&4Your"));
			}
			if (messages.getString("admin.join.success").startsWith("&6that")) {
				messages.set("admin.join.success",
						messages.getString("admin.join.success").replace("&6your", "&6Your"));
			}
			if (messages.getString("admin.promote.owner").contains("&6To")) {
				messages.set("admin.promote.owner", messages.getString("admin.promote.owner").replace("&6To", "&6to"));
			}

			if (messages.getString("help.delhome").contains("teams")) {
				messages.set("help.delhome", messages.getString("help.delhome").replace("teams", "team's"));
			}

			messages.set("admin.chest.disable.already", "&4Chest claims are already disabled");
			messages.set("admin.chest.disable.success", "&6Chest claiming has been disabled");
			messages.set("admin.chest.disabled.bc", "&6&lAll claimed chests are able to be opened");
			messages.set("admin.chest.enable.already", "&4Chest claims are already enabled");
			messages.set("admin.chest.enable.success", "&6Chest claiming has been enabled");
			messages.set("admin.chest.enabled.bc", "&6&lAll claimed chests are locked");
		case 15:
			messages.set("echest.echest", "Enderchest");
			messages.set("pvp.enabled", "&6Pvp has been enabled for your team");
			messages.set("pvp.disabled", "&6Pvp has been disabled for your team");
		case 16:
			messages.set("baltop.leaderboard", "&6Leaderboard");
			messages.set("baltop.syntax", "&b%s: &6%s &7(%s)");
			messages.set("baltop.divide", "&f...");
			messages.set("info.level", "&6Level: &b%s");
			messages.set("rankup.max", "&4You are already the max rank");
			messages.set("rankup.score", "&4You need %s score to rankup");
			messages.set("rankup.money", "&4You need %s money in the team balance to rankup");
			messages.set("rankup.success", "&6Your team has leveled up");
			messages.set("admin.setrank.success", "&6That teams rank has been set");
			messages.set("admin.setrank.no", "&4That rank either does not exist or is not setup correctly");
		case 17:
			messages.set("rank.infos", "&6Team rank: &b%s &7(%s score needed for next rankup)");
			messages.set("rank.infom", "&6Team rank: &b%s &7($%s needed for next rankup)");
			messages.set("placeholder.money", "%s ");
		case 18:
			messages.set("info.tag", "&6Tag: &b%s");
			messages.set("tag.banned", "&4That tag name is banned");
			messages.set("tag.success", "&6Your tag has succsessfully changed");
			messages.set("admin.tag.success", "&6That teams tag successfully changed");
			messages.set("tag.maxLength", "&4That tag is too long");
		case 19:
			messages.set("setwarp.char", "&4That warp name includes banned characters");
			messages.set("warp.world", "&4The location of that warp could not be found");
			messages.set("home.world", "&4You team home could not be found");
			messages.set("invite.expired", "&4The invite from &b%s has expired");
		case 20:
			messages.set("placeholder.tag", "%s");
			messages.set("placeholder.displayname", "%s");
			messages.set("description.noPerm", "&4You do not have permission to edit the description");
			messages.set("tag.noPerm", "&4You do not have permission to change the team tag");
			messages.set("name.noPerm", "&4You do not have permission to change your team name");
			messages.set("name.noPerm", "&4You do not have permission to change your team title");
		case 1000:
			// this will run only if a change has been made q
			changes = true;
			// set version the latest
			messages.set("version", 20);
			break;
		}

		// if something has been changed, saving the new config
		if (changes) {
			Bukkit.getLogger().info("Saving new messages to messages.yml");
			File f = MessageManager.getFile();
			;
			try {
				messages.save(f);
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
			}
		}

		version = 0;
		version = getConfig().getInt("version");
		changes = false;
		// use the case as the previous version of the config
		switch (version) {
		case 0:
			getConfig().set("disablePotions", true);
			getConfig().set("playerDamageSelf", true);
			getConfig().set("helpCommandColor", "b");
			getConfig().set("helpDescriptionColor", "6");
			getConfig().set("blacklist", new ArrayList<>());
			getConfig().set("maxTeamLength", 12);
		case 1:
			getConfig().set("maxHologramLines", 10);
			getConfig().set("displayTeamName", true);
		case 2:
			getConfig().set("fullyCustomHelpMessages", false);
			getConfig().set("useVault", true);
		case 3:
			getConfig().set("logTeamChat", true);
		case 4:
			getConfig().set("maxTitleLength", 10);
			getConfig().set("allowToggleTeamChat", true);
		case 5:
			getConfig().set("colorTeamName", true);
			getConfig().set("allyLimit", 5);
			getConfig().set("singleOwner", true);
			getConfig().set("tpDelay", 0);
			getConfig().set("noMove", false);
		case 6:
			getConfig().set("maxWarps", 2);
			getConfig().set("allowPassword", true);
		case 7:
			getConfig().set("zkoth.pointsPerCapture", 5);
			getConfig().set("purgeCommands", Arrays.asList(new String[] { "minecraft:give @a minecraft:dirt 1" }));
			getConfig().set("autoPurge", new ArrayList<>());
		case 8:
			getConfig().set("pointsLostByDeath", 0);
		case 9:
			getConfig().set("maxChests", 2);
			getConfig().set("allowAllyChests", true);
		case 10:
			getConfig().set("bannedChars", ",.!\"£$%^&*()[]{};:#~\\|`¬");
			getConfig().set("defaultColor", "6");
			getConfig().set("useTeams", true);
			getConfig().set("collide", true);
			getConfig().set("privateDeath", false);
			getConfig().set("privateName", false);
			getConfig().set("canSeeFriendlyInvisibles", false);
		case 11:
			getConfig().set("allowedChars", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
			getConfig().set("language", "en");
		case 12:
			getConfig().set("command.team", new ArrayList<String>());
			getConfig().set("command.teama", new ArrayList<String>(Arrays.asList("teama")));
		case 13:
			getConfig().set("events.death.score", 0);
			getConfig().set("events.death.spam", -1);
			getConfig().set("events.kill.score", 1);
			getConfig().set("events.kill.spam", 0);
			getConfig().set("spamThreshold", 60);
			getConfig().set("levels.l1.teamLimit", 10);
			getConfig().set("levels.l1.maxChests", 2);
			getConfig().set("levels.l2.teamLimit", 20);
			getConfig().set("levels.l2.maxChests", 2);
			getConfig().set("levels.l2.price", "100s");
		case 14:
			getConfig().set("maxWarps", null);
			getConfig().set("teamLimit", null);
			getConfig().set("maxChests", null);
			getConfig().set("levels.l1.maxWarps", 2);
			getConfig().set("levels.l2.maxWarps", 2);
			getConfig().set("command.teama", new ArrayList<String>(Arrays.asList("teama")));
			getConfig().set("maxTagLength", 12);
		case 15:
			getConfig().set("invite", 120);
		case 16:
			getConfig().set("maxMove", 0);
		case 1000:
			// this will run only if a change has been made
			changes = true;
			// set version the latest
			getConfig().set("version", 16);

			break;
		}

		// if something has been changed, saving the new config
		if (changes) {
			saveConfig();
		}
	}

	/**
	 * @return the config fle in which all team information is stored
	 */
	public FileConfiguration getTeams() {
		return teams;
	}

	public void saveTeams() {
		File f = new File("plugins/BetterTeams/teams.yml");
		try {
			teams.save(f);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
		}
	}

	/**
	 * Used to manage all holograms information
	 */
	public void updateHolos() {
		new HologramManager();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public void reload() {

		onDisable();
		teamManagement = null;
		reloadConfig();
		ChatManagement.enable();
		damageManagement = null;
		onEnable();

	}

	public void setupCommands() {
		ParentCommand teamCommand = new PermissionParentCommand(new CostManager("team"), new CooldownManager("team"),
				"team");
		// add all sub commands here
		teamCommand.addSubCommand(new CreateCommand());
		teamCommand.addSubCommand(new LeaveCommand());
		teamCommand.addSubCommand(new DisbandCommand());
		teamCommand.addSubCommand(new DescriptionCommand());
		teamCommand.addSubCommand(new InviteCommand());
		teamCommand.addSubCommand(new JoinCommand());
		teamCommand.addSubCommand(new NameCommand());
		teamCommand.addSubCommand(new OpenCommand());
		teamCommand.addSubCommand(new InfoCommand());
		teamCommand.addSubCommand(new KickCommand());
		teamCommand.addSubCommand(new PromoteCommand());
		teamCommand.addSubCommand(new DemoteCommand());
		teamCommand.addSubCommand(new HomeCommand());
		teamCommand.addSubCommand(new SethomeCommand());
		teamCommand.addSubCommand(new BanCommand());
		teamCommand.addSubCommand(new UnbanCommand());
		teamCommand.addSubCommand(new ChatCommand());
		teamCommand.addSubCommand(new ColorCommand());
		teamCommand.addSubCommand(new TitleCommand());
		teamCommand.addSubCommand(new TopCommand());
		teamCommand.addSubCommand(new BaltopCommand());
		teamCommand.addSubCommand(new RankCommand());
		teamCommand.addSubCommand(new DelHome());
		teamCommand.addSubCommand(new AllyCommand());
		teamCommand.addSubCommand(new NeutralCommand());
		teamCommand.addSubCommand(new AllyChatCommand());
		teamCommand.addSubCommand(new ListCommand());
		teamCommand.addSubCommand(new WarpCommand());
		teamCommand.addSubCommand(new SetWarpCommand());
		teamCommand.addSubCommand(new DelwarpCommand());
		teamCommand.addSubCommand(new WarpsCommand());
		teamCommand.addSubCommand(new EchestCommand());
		teamCommand.addSubCommand(new RankupCommand());
		teamCommand.addSubCommand(new TagCommand());

		if (getConfig().getBoolean("disableCombat")) {
			teamCommand.addSubCommand(new PvpCommand());
		}
		// only used if a team is only allowed a single owner
		if (getConfig().getBoolean("singleOwner")) {
			teamCommand.addSubCommand(new SetOwnerCommand());
		}

		ParentCommand chest = new PermissionParentCommand("chest");
		chest.addSubCommand(new ChestClaimCommand());
		chest.addSubCommand(new ChestRemoveCommand());
		chest.addSubCommand(new ChestRemoveallCommand());
		teamCommand.addSubCommand(chest);

		new BooksawCommand("team", teamCommand, "betterteams.standard", "All commands for teams",
				getConfig().getStringList("command.team"));

		ParentCommand teamaCommand = new ParentCommand("teamadmin");

		teamaCommand.addSubCommand(new ReloadTeama());
		teamaCommand.addSubCommand(new ChatSpyTeama());
		teamaCommand.addSubCommand(new TitleTeama());
		teamaCommand.addSubCommand(new VersionTeama());
		teamaCommand.addSubCommand(new HomeTeama());
		teamaCommand.addSubCommand(new NameTeama());
		teamaCommand.addSubCommand(new DescriptionTeama());
		teamaCommand.addSubCommand(new OpenTeama());
		teamaCommand.addSubCommand(new InviteTeama());
		teamaCommand.addSubCommand(new CreateTeama());
		teamaCommand.addSubCommand(new JoinTeama());
		teamaCommand.addSubCommand(new LeaveTeama());
		teamaCommand.addSubCommand(new PromoteTeama());
		teamaCommand.addSubCommand(new DemoteTeama());
		teamaCommand.addSubCommand(new WarpTeama());
		teamaCommand.addSubCommand(new SetwarpTeama());
		teamaCommand.addSubCommand(new DelwarpTeama());
		teamaCommand.addSubCommand(new PurgeTeama());
		teamaCommand.addSubCommand(new DisbandTeama());
		teamaCommand.addSubCommand(new ColorTeama());
		teamaCommand.addSubCommand(new EchestTeama());
		teamaCommand.addSubCommand(new SetrankTeama());
		teamaCommand.addSubCommand(new TagTeama());

		if (getConfig().getBoolean("singleOwner")) {
			teamaCommand.addSubCommand(new SetOwnerTeama());
		}

		ParentCommand teamaScoreCommand = new ParentCommand("score");
		teamaScoreCommand.addSubCommand(new AddScore());
		teamaScoreCommand.addSubCommand(new SetScore());
		teamaScoreCommand.addSubCommand(new RemoveScore());
		teamaCommand.addSubCommand(teamaScoreCommand);

		ParentCommand teamaChestCommand = new ParentCommand("chest");
		teamaChestCommand.addSubCommand(new ChestClaimTeama());
		teamaChestCommand.addSubCommand(new ChestRemoveTeama());
		teamaChestCommand.addSubCommand(new ChestRemoveallTeama());
		teamaChestCommand.addSubCommand(new ChestEnableClaims());
		teamaChestCommand.addSubCommand(new ChestDisableClaims());
		teamaCommand.addSubCommand(teamaChestCommand);

		if (useHolographicDisplays) {
			ParentCommand teamaHoloCommand = new ParentCommand("holo");
			teamaHoloCommand.addSubCommand(new CreateHoloTeama());
			teamaHoloCommand.addSubCommand(new RemoveHoloTeama());
			teamaCommand.addSubCommand(teamaHoloCommand);
		}

		if (econ != null) {
			teamCommand.addSubCommand(new DepositCommand());
			teamCommand.addSubCommand(new BalCommand());
			teamCommand.addSubCommand(new WithdrawCommand());
		}

		new BooksawCommand("teamadmin", teamaCommand, "betterteams.admin", "All admin commands for teams",
				getConfig().getStringList("command.teama"));

	}

	public void setupListeners() {
		Bukkit.getLogger().info("Display team name config value: " + getConfig().getString("displayTeamName"));
		BelowNameType type = BelowNameType.getType(getConfig().getString("displayTeamName"));
		Bukkit.getLogger().info("Loading below name. Type: " + type);
		if (getConfig().getBoolean("useTeams")) {
			if (teamManagement == null) {

				teamManagement = new MCTeamManagement(type);
				teamManagement.displayBelowNameForAll();
				getServer().getPluginManager().registerEvents(teamManagement, this);
				Bukkit.getLogger().info("teamManagement declared: " + teamManagement);
			}
		} else {
			Bukkit.getLogger().info("Not loading management");
			if (teamManagement != null) {
				Bukkit.getLogger().log(Level.WARNING, "Restart server for minecraft team changes to apply");
			}
		}

		// loading the zkoth event listener
		if (getServer().getPluginManager().isPluginEnabled("zKoth")) {
			Bukkit.getLogger().info("Found plugin zKoth, adding plugin integration");
			getServer().getPluginManager().registerEvents(new ZKothManager(), this);
		}

		getServer().getPluginManager().registerEvents((chatManagement = new ChatManagement()), this);
		getServer().getPluginManager().registerEvents(new ScoreManagement(), this);
		getServer().getPluginManager().registerEvents(new AllyManagement(), this);
		getServer().getPluginManager().registerEvents(new UpdateChecker(this), this);
		getServer().getPluginManager().registerEvents(new ChestManagement(), this);
		getServer().getPluginManager().registerEvents(new InventoryManagement(), this);
		getServer().getPluginManager().registerEvents(new RankupEvents(), this);

	}

	public void setupMetrics() {
		if (metrics == null) {
			int pluginId = 7855;
			metrics = new Metrics(this, pluginId);
			metrics.addCustomChart(new Metrics.SimplePie("language", new Callable<String>() {

				@Override
				public String call() throws Exception {
					return getConfig().getString("language");
				}
			}));

		}
	}
}
