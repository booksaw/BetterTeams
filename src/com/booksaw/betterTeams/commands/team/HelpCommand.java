package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.commands.SubCommand;

import net.md_5.bungee.api.ChatColor;

public class HelpCommand implements SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		sender.sendMessage(createHelpMessage(label, "help", "View the this help page"));

		return null;
	}

	/**
	 * Used to create a formatted help message to explain what a command does to the
	 * user
	 * 
	 * @param label       the base command
	 * @param commandPath the rest of the command (i.e. help \<param\>)
	 * @param description the description of the command
	 * @return the created message relating to that command
	 */
	public String createHelpMessage(String label, String commandPath, String description) {
		return ChatColor.AQUA + "/" + label + " " + commandPath + ChatColor.WHITE + " - " + ChatColor.GOLD
				+ description;
	}

	@Override
	public String getCommand() {
		return null;
	}

}
