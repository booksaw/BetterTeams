package com.booksaw.betterTeams;

import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to register a command which uses the sub command system
 *
 * @author booksaw
 */
public class BooksawCommand extends BukkitCommand {
	private final SubCommand subCommand;

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
	public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
		// running custom command manager
		if (checkPointers(sender, label, args)) {
			// if pointers were found and dealt with
			return true;
		}

		CommandResponse response;
		if (subCommand instanceof ParentCommand) {
			response = ((ParentCommand) subCommand).onCommand(sender, label, args, true);
		} else {
			response = subCommand.onCommand(sender, label, args);
		}

		if (response != null)
			response.sendResponseMessage(sender);
		return true;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, String[] args) {
		List<String> options = new ArrayList<>();
		subCommand.onTabComplete(options, sender, label, args);

		return options;
	}

	public boolean checkPointers(CommandSender sender, String label, String[] args) {

		if (!sender.hasPermission("betterteams.admin.selector")) {
			return false;
		}

		for (int i = 0; i < args.length; i++) {
			String str = args[i];

			if (!str.startsWith("@")) {
				continue;
			}
			// a selector is found
			boolean found = false;
			for (Entity e : Bukkit.selectEntities(sender, str)) {
				if (e instanceof Player) {
					found = true;
					String[] newArgs = args.clone();
					newArgs[i] = e.getName();
					execute(sender, label, newArgs);

				}
			}

			return found;
		}

		// no selector was found
		return false;

	}

}