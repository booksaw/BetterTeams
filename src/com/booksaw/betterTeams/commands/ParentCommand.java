package com.booksaw.betterTeams.commands;

import java.util.HashMap;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.MessageManager;

/**
 * This is used for any parent commands across the system
 * 
 * @author booksaw
 *
 */
public class ParentCommand implements SubCommand {

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
	public ParentCommand(String command, SubCommand helpCommand) {
		this.command = command;
		subCommands.put("help", helpCommand);
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
			subCommands.get("help").onCommand(sender, label, args);
			return null;
		}

		SubCommand command = subCommands.get(args[0]);
		if (command == null) {
			subCommands.get("help").onCommand(sender, label, args);
			return null;
		}

		args = removeFirstElement(args);
		String result = command.onCommand(sender, label, args);
		MessageManager.sendMessasge(sender, result);
		return null;
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

	@Override
	public String getCommand() {
		return command;
	}

}
