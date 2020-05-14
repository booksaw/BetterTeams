package com.booksaw.betterTeams.commands.teama;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.commands.SubCommand;

import net.md_5.bungee.api.ChatColor;

public class HelpTeama extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		sender.sendMessage(createHelpMessage(label, "help", "View the this help page"));
		sender.sendMessage(createHelpMessage(label, "reload", "Reload the better teams config"));

		return null;
	}

	@Override
	public String getCommand() {
		return "help";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
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
}
