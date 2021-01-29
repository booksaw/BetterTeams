package com.booksaw.betterTeams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;

import com.booksaw.betterTeams.commands.SubCommand;

/**
 * Used to register a command which uses the sub command system
 * 
 * @author booksaw
 *
 */
public class BooksawCommand extends BukkitCommand {
	private SubCommand subCommand;

	public BooksawCommand(String command, SubCommand subCommand, String permission, String description,
			List<String> alises) {
		super(command);
		this.description = description;
		usageMessage = "/<command> help";
		setPermission(permission);
		setAliases(alises);
		this.subCommand = subCommand;

		((CraftServer) Main.plugin.getServer()).getCommandMap().register(command, this);
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		// running custom command manager
		CommandResponse response = subCommand.onCommand(sender, label, args);
		if (response != null)
			response.sendResponseMessage(sender);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String label, String[] args) {
		List<String> options = new ArrayList<>();
		subCommand.onTabComplete(options, sender, label, args);

		return options;
	}

}