package com.booksaw.betterTeams;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.booksaw.betterTeams.commands.CommandTeam;
import com.booksaw.betterTeams.commands.CommandTeamAdmin;
import com.booksaw.betterTeams.events.ChatManagement;
import com.booksaw.betterTeams.events.DamageManagement;

public class Main extends JavaPlugin {

	public static Main plugin;

	private DamageManagement damageManagement;

	@Override
	public void onEnable() {

		saveDefaultConfig();
		plugin = this;

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new TeamPlaceholders(this).register();
		}

		loadCustomConfigs();
		ChatManagement.enable();
		Team.loadTeams();

		getCommand("team").setExecutor(new CommandTeam());
		getCommand("teamadmin").setExecutor(new CommandTeamAdmin());
		getServer().getPluginManager().registerEvents(new ChatManagement(), this);

	}

	/**
	 * This is used to store the config file in which the t
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
		case 1000:
			// this will run only if a change has been made
			changes = true;
			// set version the latest
			messages.set("version", 2);
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
		System.out.println("version = " + version);
		// use the case as the previous version of the config
		switch (version) {
		case 0:
			getConfig().set("disablePotions", true);
			getConfig().set("playerDamageSelf", true);
		case 1000:
			// this will run only if a change has been made
			changes = true;
			// set version the latest
			getConfig().set("version", 1);
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

}
