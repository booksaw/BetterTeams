package com.booksaw.betterTeams;

import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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
	public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
		// running custom command manager
		if (checkPointers(sender, label, args)) {
			// if pointers were found and dealt with
			return true;
		}

		boolean async = subCommand.checkAsync(args);

		if (async) {
			Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> runExecution(sender, label, args));
		} else {
			runExecution(sender, label, args);
		}

		return true;
	}

	public void runExecution(CommandSender sender, String label, String[] args) {
		try {
			CommandResponse response;
			if (subCommand instanceof ParentCommand) {
				response = ((ParentCommand) subCommand).onCommand(sender, label, args, true);
			} else {
				response = subCommand.onCommand(sender, label, args);
			}

			if (response != null)
				response.sendResponseMessage(sender);
		} catch (Exception e) {
			Main.plugin.getLogger().severe(
					"Something went wrong while executing the command, please report this https://github.com/booksaw/BetterTeams/issues/new/choose");
			e.printStackTrace();
			MessageManager.sendMessage(sender, "internalError");
		}
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, String[] args) {
		List<String> options = new ArrayList<>();
		subCommand.onTabComplete(options, sender, label, args);

		return options;
	}

	public boolean checkPointers(@NotNull CommandSender sender, String label, String[] args) {
		if (!sender.hasPermission("betterteams.admin.selector")) {
			return false;
		}

		for (String str : args) {
			if (!str.startsWith("@")) {
				continue;
			}
			// a selector is found
			boolean found = false;
			try {
				for (Entity e : Bukkit.selectEntities(sender, str)) {
					if (e instanceof Player) {
						found = true;
						String[] newArgs = args.clone();
						for (int j = 0; j < newArgs.length; j++) {
							if (newArgs[j].equals(str)) {
								newArgs[j] = e.getName();
							}
						}

						execute(sender, label, newArgs);

					}
				}
			} catch (Exception e) {
				return false;
			}

			return found;
		}

		// no selector was found
		return false;
	}
}