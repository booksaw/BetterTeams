package com.booksaw.betterTeams;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

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

		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

			commandMap.register(command, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
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