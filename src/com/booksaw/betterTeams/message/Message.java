package com.booksaw.betterTeams.message;

import org.bukkit.command.CommandSender;

/**
 * This class is used to handle the multiple types of message which the plugin
 * contains
 * 
 * @since 3.1.0
 * @author booksaw
 *
 */
public interface Message {

	/**
	 * Used to send the message
	 * 
	 * @param sender the player to send the message to
	 */
	public void sendMessage(CommandSender sender);

}
