package com.booksaw.betterTeams;

import org.bukkit.plugin.java.JavaPlugin;

import com.booksaw.betterTeams.commands.CommandTeam;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	public static Main pl;

	@Override
	public void onEnable() {

		saveDefaultConfig();
		pl = this;
		addMessages();
		Team.loadTeams();
		getCommand("team").setExecutor(new CommandTeam());

	}

	/**
	 * This is used to add messages and their references to the the message manager,
	 * in future this will be converted into a lanaguage file
	 */
	public void addMessages() {
		// general errors
		MessageManager.addMessage("invalidArg", ChatColor.RED + "Invalid Arguments, help:");

		// for /team create
		MessageManager.addMessage("create.exists", "That team already exists");
		MessageManager.addMessage("create.success", "Your team has been created");
		MessageManager.addMessage("create.leave",
				"You are already in a team, leave that team first to create a new one");

		// for /team leave
		MessageManager.addMessage("leave.inTeam", "You must be in a team to do that");
		MessageManager.addMessage("leave.success", "You have left the team");
		MessageManager.addMessage("leave.lastOwner",
				"You are the only owner rank within the team, Either promote someone else or use " + ChatColor.AQUA
						+ "/team disband " + ChatColor.GOLD + "to disband the team");
	}
}
