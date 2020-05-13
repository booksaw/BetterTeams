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
		MessageManager.addMessage("inTeam", "You must be in a team to do that");
		MessageManager.addMessage("needOwner", "You must be the owner of the team to do that");
		MessageManager.addMessage("needPlayer", "You must be a player to do that");

		// for /team create
		MessageManager.addMessage("create.exists", "That team already exists");
		MessageManager.addMessage("create.success", "Your team has been created");
		MessageManager.addMessage("create.leave",
				"You are already in a team, leave that team first to create a new one");

		// for /team leave
		MessageManager.addMessage("leave.success", "You have left the team");
		MessageManager.addMessage("leave.lastOwner",
				"You are the only owner rank within the team, Either promote someone else or use " + ChatColor.AQUA
						+ "/team disband " + ChatColor.GOLD + "to disband the team");

		// for /team disband
		MessageManager.addMessage("disband.success", "You have disbanded the team");
		MessageManager.addMessage("disband.confirm",
				"Type " + ChatColor.AQUA + "/team disband " + ChatColor.GOLD + "again to confirm");

		// for /team description
		MessageManager.addMessage("description.success", "You have changed the team description");
		MessageManager.addMessage("description.view", "Team Description: %s");
		MessageManager.addMessage("description.noDesc", "No team description set");
	}
}
