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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.team.BanCommand;
import com.booksaw.betterTeams.commands.team.ChatCommand;
import com.booksaw.betterTeams.commands.team.CreateCommand;
import com.booksaw.betterTeams.commands.team.DemoteCommand;
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
import com.booksaw.betterTeams.commands.team.SethomeCommand;
import com.booksaw.betterTeams.commands.team.UnbanCommand;
import com.booksaw.betterTeams.commands.teama.CreateHoloTeama;
import com.booksaw.betterTeams.commands.teama.ReloadTeama;
import com.booksaw.betterTeams.commands.teama.RemoveHoloTeama;
import com.booksaw.betterTeams.events.BelowNameManagement;
import com.booksaw.betterTeams.events.ChatManagement;
import com.booksaw.betterTeams.events.DamageManagement;
import com.booksaw.betterTeams.events.ScoreManagement;
import com.booksaw.betterTeams.events.BelowNameManagement.BelowNameType;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class Main extends JavaPlugin {

	public static Main plugin;
	public boolean useHolographicDisplays;

	private DamageManagement damageManagement;
	public BelowNameManagement nameManagement;

	@Override
	public void onEnable() {

		saveDefaultConfig();
		plugin = this;

		loadCustomConfigs();
		ChatManagement.enable();
		Team.loadTeams();

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new TeamPlaceholders(this).register();
			updateHolos();
		}

		useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

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

		new BooksawCommand(getCommand("team"), teamCommand);

		ParentCommand teamaCommand = new ParentCommand("teamadmin");

		teamaCommand.addSubCommand(new ReloadTeama());

		if (useHolographicDisplays) {
			ParentCommand teamaHoloCommand = new ParentCommand("holo");
			teamaHoloCommand.addSubCommand(new CreateHoloTeama());
			teamaHoloCommand.addSubCommand(new RemoveHoloTeama());
			teamaCommand.addSubCommand(teamaHoloCommand);
		}

		new BooksawCommand(getCommand("teamadmin"), teamaCommand);
		getServer().getPluginManager().registerEvents(new ChatManagement(), this);
		getServer().getPluginManager().registerEvents(new ScoreManagement(), this);

		BelowNameType type = BelowNameType.getType(getConfig().getString("displayTeamName"));
		if (type != BelowNameType.FALSE) {
			if (nameManagement == null) {
				nameManagement = new BelowNameManagement(type);
				nameManagement.displayBelowNameForAll();
				getServer().getPluginManager().registerEvents(nameManagement, this);
			}
		} else {
			if (nameManagement != null) {
				Bukkit.getLogger().log(Level.WARNING, "Restart server for name changes to apply");
			}
		}

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

		case 1000:
			// this will run only if a change has been made
			changes = true;
			// set version the latest
			messages.set("version", 4);
			break;
		}

		// if something has been changed, saving the new config
		if (changes) {
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
		case 1000:
			// this will run only if a change has been made
			changes = true;
			// set version the latest
			getConfig().set("version", 2);
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

}
