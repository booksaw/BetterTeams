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
	 * in future this will be converted into a language file
	 */
	public void addMessages() {
		// general errors
		MessageManager.addMessage("invalidArg", ChatColor.RED + "Invalid Arguments, help:");
		MessageManager.addMessage("inTeam", ChatColor.RED + "You must be in a team to do that");
		MessageManager.addMessage("notInTeam", ChatColor.RED + "You must leave your team before doing that");
		MessageManager.addMessage("needOwner", ChatColor.RED + "You must be the owner of the team to do that");
		MessageManager.addMessage("needAdmin", ChatColor.RED + "You must be admin or owner of the team to do that");
		MessageManager.addMessage("needPlayer", ChatColor.RED + "You must be a player to do that");
		MessageManager.addMessage("noPlayer", ChatColor.RED + "Specified player not found");
		MessageManager.addMessage("notTeam",
				ChatColor.RED + "That team does not exist try" + ChatColor.AQUA + "/team create <name>");

		// for /team create <name>
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

		// for /team name
		MessageManager.addMessage("name.success", "You have changed the team name");
		MessageManager.addMessage("name.view", "Team name: %s");
		MessageManager.addMessage("name.exists", ChatColor.RED + "That team already exists");

		// for /team invite [player]
		MessageManager.addMessage("invite.success", "That player has been invited");
		MessageManager.addMessage("invite.invite", "You have been invited to join team %s do " + ChatColor.AQUA
				+ "/team join <team> " + ChatColor.GOLD + " to join the team");
		MessageManager.addMessage("invite.inTeam", "That player is already in a team");

		// for /team join <team>
		MessageManager.addMessage("join.success", "You have joined that team");
		MessageManager.addMessage("join.notify", "Welcome " + ChatColor.AQUA + "%s " + ChatColor.GOLD + "to the team!");
		MessageManager.addMessage("join.notInvited", ChatColor.RED + "You have not been invited to that team");
	}
}
