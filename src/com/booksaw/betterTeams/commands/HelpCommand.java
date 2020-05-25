package com.booksaw.betterTeams.commands;

import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.Main;

import net.md_5.bungee.api.ChatColor;

public class HelpCommand extends SubCommand {

	ParentCommand command;
	ChatColor prefix, description;

	public HelpCommand(ParentCommand command) {
		this.command = command;
		prefix = ChatColor.getByChar(Main.plugin.getConfig().getString("helpCommandColor").charAt(0));
		description = ChatColor.getByChar(Main.plugin.getConfig().getString("helpDescriptionColor").charAt(0));

	}

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		for (Entry<String, SubCommand> subCommand : command.getSubCommands().entrySet()) {
			if (sender.hasPermission("betterTeams." + subCommand.getValue().getNode())) {
				sender.sendMessage(
						createHelpMessage(label, subCommand.getKey() + " " + subCommand.getValue().getArguments(),
								subCommand.getValue().getHelpMessage()));
			}
		}

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
		return prefix + "/" + label + " " + commandPath + ChatColor.WHITE + " - " + this.description + description;
	}

	@Override
	public String getCommand() {
		return "help";
	}

	@Override
	public String getNode() {
		return "";
	}

	@Override
	public String getHelp() {
		return "View this help page";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

}
