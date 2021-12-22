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
	private final boolean prefix;

	/**
	 * @param message The message to send to users
	 * @param prefix  if the message should include the prefix or not
	 */
	public StaticMessage(String message, boolean prefix) {
		this.message = message;
		this.prefix = prefix;
	}

	/**
	 * @param message The message to send to users
	 */
	public StaticMessage(String message) {
		this.message = message;
		prefix = false;
	}

	@Override
	public void sendMessage(CommandSender sender) {
		MessageManager.sendFullMessage(sender, message, prefix);

	}

}
