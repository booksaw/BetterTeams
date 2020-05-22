package com.booksaw.betterTeams.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.commands.teama.ReloadTeama;

public class CommandTeamAdmin implements CommandExecutor {
	private ParentCommand teamCommand;

	public CommandTeamAdmin() {
		teamCommand = new ParentCommand("teamadmin");

		teamCommand.addSubCommand(new ReloadTeama());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		teamCommand.onCommand(sender, label, args);
		return true;
	}

}
