package com.booksaw.betterTeams;

import org.bukkit.plugin.java.JavaPlugin;

import com.booksaw.betterTeams.commands.CommandTeam;
import com.booksaw.betterTeams.events.ChatManagement;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	public static Main pl;

	@Override
	public void onEnable() {

		saveDefaultConfig();
		pl = this;
		addMessages();
		Team.loadTeams();
		ChatManagement.enable();
		
		getCommand("team").setExecutor(new CommandTeam());
		getServer().getPluginManager().registerEvents(new ChatManagement(), this);

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

		MessageManager.addMessage("needSameTeam", "You are not in the same team as that person");

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
		MessageManager.addMessage("invite.banned", "That player is banned from your team");

		// for /team join <team>
		MessageManager.addMessage("join.success", "You have joined that team");
		MessageManager.addMessage("join.notify", "Welcome " + ChatColor.AQUA + "%s " + ChatColor.GOLD + "to the team!");
		MessageManager.addMessage("join.notInvited", ChatColor.RED + "You have not been invited to that team");
		MessageManager.addMessage("join.banned", ChatColor.RED + "You are banned from that team");

		// for /team open
		MessageManager.addMessage("open.successopen", "Your team now open to everyone");
		MessageManager.addMessage("open.successclose", "Your team is now invite only");

		// for /team info [team / player]
		MessageManager.addMessage("info.name", "Name: " + ChatColor.AQUA + "%s");
		MessageManager.addMessage("info.description", "Description: " + ChatColor.AQUA + "%s");
		MessageManager.addMessage("info.open", "Open: " + ChatColor.AQUA + "%s");
		MessageManager.addMessage("info.owner", "Owners: " + ChatColor.AQUA + "%s");
		MessageManager.addMessage("info.admin", "Admins: " + ChatColor.AQUA + "%s");
		MessageManager.addMessage("info.default", "Users: " + ChatColor.AQUA + "%s");
		MessageManager.addMessage("info.needTeam", "That player is not in a team");
		MessageManager.addMessage("info.fail", "No team or player found under that name");

		// for /team kick <player>
		MessageManager.addMessage("kick.success", "That player has been kicked");
		MessageManager.addMessage("kick.notify", "You have been kicked from team %s");
		MessageManager.addMessage("kick.noPerm", "You do not have permission to kick that person");

		// for /team ban <player>
		MessageManager.addMessage("ban.success", "That player has been banned");
		MessageManager.addMessage("ban.notify", "You have been banned from team %s");
		MessageManager.addMessage("ban.noPerm", "You do not have permission to ban that person");
		MessageManager.addMessage("ban.already", "That player is already banned");

		// for /team unban <player>
		MessageManager.addMessage("unban.success", "That player has been unbanned");
		MessageManager.addMessage("unban.notify", "You have been unbanned from team %s");
		MessageManager.addMessage("unban.noPerm", "You do not have permission to unban that person");
		MessageManager.addMessage("unban.not", "That player is not banned");

		// for /team promote <player>
		MessageManager.addMessage("promote.success", "That player has been promoted");
		MessageManager.addMessage("promote.noPerm", "You do not have permissions to promote that person");
		MessageManager.addMessage("promote.max", "That person is already promoted to the max!");
		MessageManager.addMessage("promote.notify", "You have been promoted!");

		// for /team demote <player>
		MessageManager.addMessage("demote.success", "That player has been demoted");
		MessageManager.addMessage("demote.noPerm", "You do not have permissions to demote that person");
		MessageManager.addMessage("demote.min", "That person is already the lowest rank");
		MessageManager.addMessage("demote.notify", "You have been demoted");
		MessageManager.addMessage("demote.lastOwner", "You cannot demote the final owner, promote someone else first");

		// for /team home
		MessageManager.addMessage("home.success", "You have been teleported");
		MessageManager.addMessage("home.noHome", "Your team has not set a home");

		// for /team sethome
		MessageManager.addMessage("sethome.success", "Your team home has been set");
		MessageManager.addMessage("sethome.noPerm", "Your are not a high enough rank to set your team home");

		// for /team chat [message]
		MessageManager.addMessage("chat.enabled", "Your messages now go to the team chat");
		MessageManager.addMessage("chat.disabled", "Your messages now go to the global chat");

		// for chat formatting
		MessageManager.addMessage("chat.syntax", ChatColor.AQUA + "[Team] " + ChatColor.WHITE + "%s: %s");
	}
}
