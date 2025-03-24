package com.booksaw.betterTeams.commands;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.cooldown.CommandCooldown;
import com.booksaw.betterTeams.cooldown.CooldownManager;
import com.booksaw.betterTeams.cost.CommandCost;
import com.booksaw.betterTeams.cost.CostManager;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * This is used for any parent commands across the system
 *
 * @author booksaw
 */
public class ParentCommand extends SubCommand {

	/**
	 * Used to store all applicable sub commands
	 */
	@Getter
	private final HashMap<String, SubCommand> subCommands = new HashMap<>();

	/**
	 * Used to store what the parent command reference is
	 */
	@Getter
	private final String command;

	private CooldownManager cooldowns = null;
	private CostManager prices = null;

	private boolean runAsync = false;

	/**
	 * Creates a new parent command with a set of sub commands
	 *
	 * @param command the command which will be defaulted to if the user enters an
	 *                incorrect command
	 */
	public ParentCommand(String command) {
		this.command = command;
		subCommands.put("help", new HelpCommand(this));
	}

	public ParentCommand(CostManager prices, CooldownManager cooldowns, String command, boolean runAsync) {
		this(command);
		this.cooldowns = cooldowns;
		this.prices = prices;
		this.runAsync = runAsync;
	}

	/**
	 * Add multiple subcommands (see
	 * {@link ParentCommand#addSubCommand(SubCommand)})
	 *
	 * @param commands The command(s) to add
	 */
	public void addSubCommands(SubCommand... commands) {
		for (SubCommand command : commands) {
			addSubCommand(command);
		}
	}

	/**
	 * this method adds another command to the parent command
	 *
	 * @param command the command to add
	 */
	public void addSubCommand(SubCommand command) {
		subCommands.put(getReference(command), command);
	}

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		return onCommand(sender, label, args, false);
	}

	public CommandResponse onCommand(CommandSender sender, String label, String[] args, boolean first) {

		// checking length
		if (args.length == 0) {
			// help command is not expected to return anything
			displayHelp(sender, label, args);
			return null;
		}

		if (!first) {
			label = label + " " + getCommand();
		}

		SubCommand command = subCommands.get(args[0].toLowerCase());
		if (command == null) {
			displayHelp(sender, label, args);
			return null;
		}

		if (!sender.hasPermission("betterteams." + command.getNode()) && !command.getNode().isEmpty()) {
			MessageManager.sendMessage(sender, "noPerm");
			return null;
		}

		String[] newArgs = removeFirstElement(args);
		// checking enough arguments have been entered
		if (command.getMinimumArguments() > newArgs.length) {
			MessageManager.sendMessage(sender, "invalidArg");
			displayHelp(sender, label, args);
			return null;
		} else if (command.needPlayer() && !(sender instanceof Player)) {
			return new CommandResponse("needPlayer");
		}

		if (cooldowns != null && sender instanceof Player) {
			CommandCooldown cooldown = cooldowns.getCooldown(command.getCommand());
			int remaining = cooldown.getRemaining((Player) sender);
			if (remaining != -1) {
				return new CommandResponse(false, new ReferencedFormatMessage("cooldown.wait", remaining));
			}

		}

		if (prices != null && sender instanceof Player) {
			CommandCost price = prices.getCommandCost(command.getCommand());
			if (!price.hasBalance((Player) sender)) {
				return new CommandResponse("cost.tooPoor");
			}
		}

		CommandResponse result = command.onCommand(sender, label, newArgs);

		if (result == null) {
			return null;
		}

		if (prices != null && sender instanceof Player && result.wasSuccessful()) {
			CommandCost price = prices.getCommandCost(command.getCommand());
			if (!price.runCommand((Player) sender)) {
				return new CommandResponse("cost.tooPoor");
			}
			if (price.getCost() > 0) {
				NumberFormat formatter = NumberFormat.getCurrencyInstance();
				MessageManager.sendMessage(sender, "cost.run", formatter.format(price.getCost()));
			}
		}

		if (cooldowns != null && sender instanceof Player) {
			CommandCooldown cooldown = cooldowns.getCooldown(command.getCommand());
			cooldown.runCommand((Player) sender);
		}

		return result;
	}

	/**
	 * Used to display the help information to the user
	 *
	 * @param sender the user which called the command
	 * @param label  the label of the command
	 * @param args   the arguments that the user entered
	 */
	private void displayHelp(CommandSender sender, String label, String[] args) {
		subCommands.get("help").onCommand(sender, label, args);
	}

	/**
	 * Used to remove the first element, this is used when sending commands into sub
	 * commands
	 */
	private String[] removeFirstElement(String[] args) {
		String[] toReturn = new String[args.length - 1];
		System.arraycopy(args, 1, toReturn, 0, toReturn.length);
		return toReturn;

	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "";
	}

	@Override
	public String getHelp() {
		return "";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length <= 1 || args[0].isEmpty()) {
			for (Entry<String, SubCommand> subCommand : subCommands.entrySet()) {
				if ((args.length == 0 || subCommand.getKey().startsWith(args[0]))
						&& sender.hasPermission("betterTeams." + subCommand.getValue().getNode()))
					options.add(subCommand.getKey());
			}
			return;
		}

		SubCommand command = subCommands.get(args[0]);
		if (command == null) {
			return;
		}

		if ((args.length - 1 > command.getMaximumArguments() && command.getMaximumArguments() >= 0)
				|| (command.needPlayer() && !(sender instanceof Player))) {
			return;
		}

		command.onTabComplete(options, sender, label, removeFirstElement(args));
	}

	@Override
	public int getMaximumArguments() {
		return -1;
	}

	public String getReference(SubCommand subCommand) {
		String toReturn = MessageManager.getMessage("command." + subCommand.getCommand());
		if (!toReturn.isEmpty()) {
			return toReturn;
		}

		MessageManager.getDefaultMessages().set("command." + subCommand.getCommand(), subCommand.getCommand());

		File f = MessageManager.getFile();
		try {
			MessageManager.getDefaultMessages().save(f);
		} catch (IOException ex) {
			Main.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
		}

		return subCommand.getCommand();

	}

	@Override
	protected boolean runAsync(String[] args) {
		return runAsync;
	}

	@Override
	public boolean checkAsync(String[] args) {
		if (args.length == 0) {
			return true;
		}
		SubCommand command = subCommands.get(args[0].toLowerCase());
		if (command == null) {
			return true;
		}
		return command.runAsync(removeFirstElement(args));

	}

}
