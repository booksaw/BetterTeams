package com.booksaw.betterTeams.message;

import org.bukkit.command.CommandSender;

/**
 * This class is used for any messages which should be sent as normal (literally
 * just a message)
 *
 * @author booksaw
 */
public class StaticMessage implements Message {

	final String message;

	/**
	 * @param message The message to send to users
	 */
	public StaticMessage(String message) {
		this.message = message;
	}

	@Override
	public void sendMessage(CommandSender sender) {
		MessageManager.sendFullMessage(sender, message);

	}

}
