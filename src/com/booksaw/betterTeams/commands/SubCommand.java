package com.booksaw.betterTeams.commands;

import org.bukkit.command.CommandSender;

/**
 * This class is used by any commands which are included within a command tree
 * (for example /command subcommand)
 * 
 * @author booksaw
 *
 */
public interface SubCommand {

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
	public String onCommand(CommandSender sender, String label, String[] args);

	/**
	 * @return the sub-command which this class handles
	 */
	public String getCommand();
}
