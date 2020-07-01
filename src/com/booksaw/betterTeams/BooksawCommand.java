package com.booksaw.betterTeams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import com.booksaw.betterTeams.commands.SubCommand;

/**
 * Used to register a command which uses the sub command system
 * 
 * @author booksaw
 *
 */
public class BooksawCommand implements CommandExecutor, TabCompleter {

	private SubCommand subCommand;

	public BooksawCommand(PluginCommand command, SubCommand subCommand) {
		this.subCommand = subCommand;

		command.setExecutor(this);
		command.setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		// running custom command manager
		CommandResponse response = subCommand.onCommand(sender, label, args);
		if (response != null)
			response.sendResponseMessage(sender);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String label, String[] args) {
		List<String> options = new ArrayList<>();
		subCommand.onTabComplete(options, sender, label, args);

		return options;
	}

}
