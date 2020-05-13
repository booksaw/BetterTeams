package com.booksaw.betterTeams.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.commands.team.CreateCommand;
import com.booksaw.betterTeams.commands.team.HelpCommand;

/**
 * This is used to direct the team command to the subCommand
 */
public class CommandTeam implements CommandExecutor {

	private ParentCommand teamCommand;

	public CommandTeam() {
		teamCommand = new ParentCommand("team", new HelpCommand());
		// add all sub commands here
		teamCommand.addSubCommand(new CreateCommand());

	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		// running custom command manager
		teamCommand.onCommand(sender, label, args);
		return true;
	}

}
