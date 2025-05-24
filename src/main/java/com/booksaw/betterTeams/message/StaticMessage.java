package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class is used for any messages which should be sent as normal (literally
 * just a message)
 *
 * @author booksaw
 */
public class StaticMessage extends StaticComponentHolderMessage {

	private final boolean prefix;

	/**
	 * @param message The message to send to users
	 * @param prefix  if the message should include the prefix or not
	 */
	public StaticMessage(String message, boolean prefix) {
		super(message);
		this.prefix = prefix;
	}

	/**
	 * @param message The message to send to users
	 */
	public StaticMessage(String message) {
		super(message);
		this.prefix = false;
	}

	@Override
	public void sendMessage(CommandSender recipient) {
		MessageManager.sendFullMessage(recipient, message, prefix);
	}

	@Override
	public void sendMessage(Collection<? extends CommandSender> recipients) {
		MessageManager.sendFullMessage(recipients, message, prefix);
	}

	@Override
	public void sendTitle(Player recipient) {
		MessageManager.sendFullTitle(recipient, message, prefix);
	}

	@Override
	public void sendTitle(Collection<Player> recipients) {
		MessageManager.sendFullTitle(recipients, message, prefix);
	}

}
