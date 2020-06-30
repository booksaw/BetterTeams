package com.booksaw.betterTeams.commands;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.cooldown.CommandCooldown;
import com.booksaw.betterTeams.cooldown.CooldownManager;
import com.booksaw.betterTeams.cost.CommandCost;
import com.booksaw.betterTeams.cost.CostManager;
import com.booksaw.betterTeams.message.MessageManager;

/**
 * This is used for any parent commands across the system
 * 
 * @author booksaw
 *
 */
public class ParentCommand extends SubCommand {

	/**
	 * Used to store all applicable sub commands
	 */
	private HashMap<String, SubCommand> subCommands = new HashMap<>();

	/**
	 * Used to store what the parent command reference is
	 */
	private String command;

	private CooldownManager cooldowns = null;
	private CostManager prices = null;

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

	public ParentCommand(CostManager prices, CooldownManager cooldowns, String command) {
		this(command);
		this.cooldowns = cooldowns;
		this.prices = prices;
	}

	/**
	 * this method adds another command to the parent command
	 * 
	 * @param command the command to add
	 */
	public void addSubCommand(SubCommand command) {
		subCommands.put(command.getCommand(), command);
	}

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		// checking length
		if (args.length == 0) {
			// help command is not expected to return anything
			displayHelp(sender, label, args);
			return null;
		}

		SubCommand command = subCommands.get(args[0].toLowerCase());
		if (command == null) {
			displayHelp(sender, label, args);
			return null;
		}

		if (!sender.hasPermission("betterteams." + command.getNode()) && !command.getNode().equals("")) {
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
			MessageManager.sendMessage(sender, "needPlayer");
			return null;
		}

		if (cooldowns != null && sender instanceof Player) {
			CommandCooldown cooldown = cooldowns.getCooldown(command.getCommand());
			int remaining = cooldown.getRemaining((Player) sender);
			if (remaining != -1) {
				MessageManager.sendMessageF(sender, "cooldown.wait", remaining + "");
				return null;
			}

		}

		if (prices != null && sender instanceof Player) {
			CommandCost price = prices.getCommandCost(command.getCommand());
			if (!price.runCommand((Player) sender)) {
				MessageManager.sendMessage(sender, "cost.tooPoor");
				return null;
			}
			if (price.getCost() > 0) {
				NumberFormat formatter = NumberFormat.getCurrencyInstance();
				MessageManager.sendMessageF(sender, "cost.run", formatter.format(price.getCost()));
			}
		}

		String result = command.onCommand(sender, label, newArgs);
		if (result == null) {
			return null;
		} else if (result.equals("help")) {
			displayHelp(sender, label, args);
			return null;
		}

		if (cooldowns != null && sender instanceof Player) {
			CommandCooldown cooldown = cooldowns.getCooldown(command.getCommand());
			cooldown.runCommand((Player) sender);
		}

		MessageManager.sendMessage(sender, result);

		return null;
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

		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = args[i + 1];
		}

		return toReturn;

	}

	public HashMap<String, SubCommand> getSubCommands() {
		return subCommands;
	}

	@Override
	public String getCommand() {
		return command;
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
		if (args.length <= 1 || args[0].equals("")) {
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
		return;
	}

	@Override
	public int getMaximumArguments() {
		return -1;
	}

}
