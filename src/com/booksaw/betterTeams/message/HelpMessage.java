package com.booksaw.betterTeams.message;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.commands.HelpCommand;
import com.booksaw.betterTeams.commands.SubCommand;

import net.md_5.bungee.api.ChatColor;

public class HelpMessage implements Message {

	final SubCommand command;
	final String label;

	public HelpMessage(SubCommand command, String label) {
		this.command = command;
		this.label = label;
	}

	@Override
	public void sendMessage(CommandSender sender) {
		MessageManager.sendFullMessage(sender, createHelpMessage(label,
				command.getCommand() + " " + command.getArguments(), command.getHelpMessage()));
	}

	/**
	 * Used to create a formatted help message to explain what a command does to the
	 * user
	 * 
	 * @param label       the base command
	 * @param commandPath the rest of the command (i.e. help [param])
	 * @param description the description of the command
	 * @return the created message relating to that command
	 */
	public String createHelpMessage(String label, String commandPath, String description) {
		return HelpCommand.prefix + "/" + label + " " + commandPath + ChatColor.WHITE + " - " + HelpCommand.description
				+ description;
	}

}
