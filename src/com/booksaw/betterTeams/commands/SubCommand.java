package com.booksaw.betterTeams.commands;

import org.bukkit.command.CommandSender;

/**
 * This class is used by any commands which are included within a command tree
 * (for example /command subcommand)
 * 
 * @author booksaw
 *
 */
public abstract class SubCommand {

	/**
	 * <p>
	 * This method is called whenever the sub command is run, return the message (+
	 * chat color if it should not be the default chat color)
	 * </p>
	 * <p>
	 * The return value should be the value of the message to be sent to the user,
	 * for more complicated messaging systems return null
	 * </p>
	 * 
	 * @param sender the person who called the command
	 * @param label  the label of the initial command (useful for help files)
	 * @param args   the arguments of the sub command (starting at args[0], as the
	 *               sub command itself will be removed)
	 * @return the message
	 */
	public abstract String onCommand(CommandSender sender, String label, String[] args);

	/**
	 * @return the sub-command which this class handles
	 */
	public abstract String getCommand();

	/**
	 * Used to get the minimum number of arguments which need to be parsed into this
	 * command
	 * 
	 * @return the number of minimum arguments
	 */
	public abstract int getMinimumArguments();

	/**
	 * Used to check if the commandSender needs to be a player, defaults to false
	 * 
	 * @return if the commandSender needs to be a player
	 */
	public boolean needPlayer() {
		return false;
	}
	
}
