package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Used when sending a message to the user which has a fixed format and is
 * loaded from messages.yml
 *
 * @author booksaw
 */
public class ReferencedFormatMessage implements Message {

	final String reference;
	final Object[] replacements;

	/**
	 * @param reference   the messages.yml reference for the command
	 * @param replacement all replacements that need to be made to the message
	 */
	public ReferencedFormatMessage(String reference, Object... replacements) {
		this.reference = reference;
		this.replacements = replacements;
	}

	@Override
	public void sendMessage(CommandSender recipient) {
		MessageManager.sendMessage(recipient, reference, replacements);
	}

	@Override
	public void sendTitle(Player recipient) {
		MessageManager.sendTitle(recipient, reference, replacements);
	}

	@Override
	public void sendMessage(Collection<? extends CommandSender> recipients) {
		MessageManager.sendMessage(recipients, reference, replacements);
	}

	@Override
	public void sendTitle(Collection<Player> recipients) {
		MessageManager.sendTitle(recipients, reference, replacements);
	}
}
