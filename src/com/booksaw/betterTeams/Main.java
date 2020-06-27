package com.booksaw.betterTeams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.booksaw.betterTeams.commands.HelpCommand;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.team.BalCommand;
import com.booksaw.betterTeams.commands.team.BanCommand;
import com.booksaw.betterTeams.commands.team.ChatCommand;
import com.booksaw.betterTeams.commands.team.ColorCommand;
import com.booksaw.betterTeams.commands.team.CreateCommand;
import com.booksaw.betterTeams.commands.team.DelHome;
import com.booksaw.betterTeams.commands.team.DemoteCommand;
import com.booksaw.betterTeams.commands.team.DepositCommand;
import com.booksaw.betterTeams.commands.team.DescriptionCommand;
import com.booksaw.betterTeams.commands.team.DisbandCommand;
import com.booksaw.betterTeams.commands.team.HomeCommand;
import com.booksaw.betterTeams.commands.team.InfoCommand;
import com.booksaw.betterTeams.commands.team.InviteCommand;
import com.booksaw.betterTeams.commands.team.JoinCommand;
import com.booksaw.betterTeams.commands.team.KickCommand;
import com.booksaw.betterTeams.commands.team.LeaveCommand;
import com.booksaw.betterTeams.commands.team.NameCommand;
import com.booksaw.betterTeams.commands.team.OpenCommand;
import com.booksaw.betterTeams.commands.team.PromoteCommand;
import com.booksaw.betterTeams.commands.team.RankCommand;
import com.booksaw.betterTeams.commands.team.SethomeCommand;
import com.booksaw.betterTeams.commands.team.TitleCommand;
import com.booksaw.betterTeams.commands.team.TopCommand;
import com.booksaw.betterTeams.commands.team.UnbanCommand;
import com.booksaw.betterTeams.commands.team.WithdrawCommand;
import com.booksaw.betterTeams.commands.teama.ChatSpyTeama;
import com.booksaw.betterTeams.commands.teama.CreateHoloTeama;
import com.booksaw.betterTeams.commands.teama.HomeTeama;
import com.booksaw.betterTeams.commands.teama.ReloadTeama;
import com.booksaw.betterTeams.commands.teama.RemoveHoloTeama;
import com.booksaw.betterTeams.commands.teama.TitleTeama;
import com.booksaw.betterTeams.commands.teama.VersionTeama;
import com.booksaw.betterTeams.events.BelowNameManagement;
import com.booksaw.betterTeams.events.BelowNameManagement.BelowNameType;
import com.booksaw.betterTeams.events.ChatManagement;
import com.booksaw.betterTeams.events.DamageManagement;
import com.booksaw.betterTeams.events.ScoreManagement;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	public static Main plugin;
	public boolean useHolographicDisplays;
	public static Economy econ = null;

	private DamageManagement damageManagement;
	public BelowNameManagement nameManagement;
	public ChatManagement chatManagement;
	Metrics metrics = null;

	@Override
	public void onEnable() {

		if (metrics == null) {
			int pluginId = 7855;
			metrics = new Metrics(this, pluginId);
		}

		saveDefaultConfig();
		plugin = this;

		loadCustomConfigs();
		ChatManagement.enable();
		Team.loadTeams();

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI").isEnabled()) {
			new TeamPlaceholders(this).register();
			updateHolos();
		}

		useHolographicDisplays = (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
				&& Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"));

		if (!setupEconomy() || !getConfig().getBoolean("useVault")) {
			econ = null;
		}

		setupListeners();
		setupCommands();
	}

	@Override
	public void onDisable() {

		if (useHolographicDisplays) {
			List<String> holos = new ArrayList<>();
			for (Hologram holo : HologramsAPI.getHolograms(this)) {
				holos.add(getString(holo.getLocation()));
			}
			teams.set("holos", holos);
			saveTeams();
		}

		if (nameManagement != null) {
			nameManagement.removeAll();
		}

	}

	/**
	 * This is used to store the config file in which the the teams data is stored
	 */
	FileConfiguration teams;

	public void loadCustomConfigs() {
		File f = new File("plugins/BetterTeams/messages.yml");

		if (!f.exists()) {
			saveResource("messages.yml", false);
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
			messages.set("prefix.default", "");
			messages.set("title.change", "&6Your title has been changed to &b%s");
			messages.set("title.success", "&6That title has been changed");
			messages.set("bannedChar", "&4A character you tried to use is banned");
			messages.set("title.tooLong", "&4That title is too long");
			messages.set("title.noFormat", "&4You do not have permission to format titles");
			messages.set("title.noColor", "&4You do not have permission to color titles");
			messages.set("admin.title.success", "&6That players title has been changed");
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
		case 1000:
			// this will run only if a change has been made
			changes = true;
			// set version the latest
			messages.set("version", 6);
			break;
		}

		// if something has been changed, saving the new config
		if (changes) {
			Bukkit.getLogger().info("Saving new messages to messages.yml");
			File f = new File("plugins/BetterTeams/messages.yml");
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
		case 1000:
			// this will run only if a change has been made
			changes = true;
			// set version the latest
			getConfig().set("version", 4);
			break;
		}

		// if something has been changed, saving the new config
		if (changes) {
			saveConfig();
		}
	}

	/**
	 * This is used to store the config file in which the t
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
		List<String> holos = teams.getStringList("holos");
		if (holos != null && holos.size() != 0) {
			for (String s : holos) {
				Location location = getLocation(s);
				Hologram holo = HologramsAPI.createHologram(Main.plugin, location);
				Team[] teams = Team.sortTeams();

				int maxHologramLines = Main.plugin.getConfig().getInt("maxHologramLines");

				holo.appendTextLine(MessageManager.getMessage("holo.leaderboard"));

				for (int i = 0; i < maxHologramLines && i < teams.length; i++) {
					holo.appendTextLine(String.format(MessageManager.getMessage("holo.syntax"), teams[i].getName(),
							teams[i].getScore()));
				}
			}

		}

		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null
						|| !Bukkit.getPluginManager().getPlugin("PlaceholderAPI").isEnabled()) {
					return;
				}
				// if no score changes have been made
				if (!Team.scoreChanges) {
					return;
				}

				Team[] teams = Team.sortTeams();

				for (Hologram holo : HologramsAPI.getHolograms(Main.plugin)) {
					holo.clearLines();

					int maxHologramLines = Main.plugin.getConfig().getInt("maxHologramLines");

					holo.appendTextLine(MessageManager.getMessage("holo.leaderboard"));

					for (int i = 0; i < maxHologramLines && i < teams.length; i++) {
						holo.appendTextLine(String.format(MessageManager.getMessage("holo.syntax"), teams[i].getName(),
								teams[i].getScore()));
					}

				}

				Team.scoreChanges = false;
			}
		}, 0L, 20 * 60L);
	}

	// returns the location of a string
	public static Location getLocation(String loc) {
		String[] split = loc.split(":");
		return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]),
				Double.parseDouble(split[3]));
	}

	// returns the string of a location
	public static String getString(Location loc) {
		return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ();
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
		reloadConfig();
		ChatManagement.enable();
		damageManagement = null;
		nameManagement = null;

		onDisable();
		onEnable();

	}

	public void setupCommands() {
		ParentCommand teamCommand = new ParentCommand("team");
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
		teamCommand.addSubCommand(new RankCommand());
		teamCommand.addSubCommand(new DelHome());

		new BooksawCommand(getCommand("team"), teamCommand);

		ParentCommand teamaCommand = new ParentCommand("teamadmin");

		teamaCommand.addSubCommand(new ReloadTeama());
		teamaCommand.addSubCommand(new ChatSpyTeama());
		teamaCommand.addSubCommand(new TitleTeama());
		teamaCommand.addSubCommand(new VersionTeama());
		teamaCommand.addSubCommand(new HomeTeama());

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

		new BooksawCommand(getCommand("teamadmin"), teamaCommand);

	}

	public void setupListeners() {
		Bukkit.getLogger().info("Display team name config value: " + getConfig().getString("displayTeamName"));
		BelowNameType type = BelowNameType.getType(getConfig().getString("displayTeamName"));
		Bukkit.getLogger().info("Loading below name. Type: " + type);
		if (type != BelowNameType.FALSE) {
			if (nameManagement == null) {

				nameManagement = new BelowNameManagement(type);
				nameManagement.displayBelowNameForAll();
				getServer().getPluginManager().registerEvents(nameManagement, this);
				Bukkit.getLogger().info("nameManagement declared: " + nameManagement);
			}
		} else {
			Bukkit.getLogger().info("Not loading management");
			if (nameManagement != null) {
				Bukkit.getLogger().log(Level.WARNING, "Restart server for name changes to apply");
			}
		}

		getServer().getPluginManager().registerEvents((chatManagement = new ChatManagement()), this);
		getServer().getPluginManager().registerEvents(new ScoreManagement(), this);
	}
}
