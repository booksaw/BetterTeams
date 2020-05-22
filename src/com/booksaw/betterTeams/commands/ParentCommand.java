package com.booksaw.betterTeams.commands;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.MessageManager;

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

	/**
	 * Creates a new parent command with a set of sub commands
	 * 
	 * @param helpCommand the command which will be defaulted to if the user enters
	 *                    an incorrect command
	 */
	public ParentCommand(String command) {
		this.command = command;
		subCommands.put("help", new HelpCommand(this));
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

		if (!sender.hasPermission("betterteams." + command.getNode())) {
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

		String result = command.onCommand(sender, label, newArgs);
		if (result == null) {
			return null;
		} else if (result.equals("help")) {
			displayHelp(sender, label, args);
			return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

}
