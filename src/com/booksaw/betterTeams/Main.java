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

	@Override
	public void onEnable() {

		saveDefaultConfig();
		plugin = this;
//		addMessages();
		loadCustomConfigs();
		ChatManagement.enable();
		Team.loadTeams();

		getCommand("team").setExecutor(new CommandTeam());
		getCommand("teamadmin").setExecutor(new CommandTeamAdmin());
		getServer().getPluginManager().registerEvents(new ChatManagement(), this);
		getServer().getPluginManager().registerEvents(new DamageManagement(), this);

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
		MessageManager.addMessages(messages);

		f = new File("plugins/BetterTeams/teams.yml");

		if (!f.exists()) {
			saveResource("teams.yml", false);
		}

		teams = YamlConfiguration.loadConfiguration(f);
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
