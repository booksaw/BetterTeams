package com.booksaw.betterTeams.message;

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
	final Object[] replacement;

	/**
	 * @param reference   the messages.yml reference for the command
	 * @param replacement all replacements that need to be made to the message
	 */
	public ReferencedFormatMessage(String reference, String... replacement) {
		this.reference = reference;
		this.replacement = replacement;
	}

	@Override
	public void sendMessage(CommandSender sender) {
		MessageManager.sendMessage(sender, reference, replacement);
	}

	@Override
	public void sendTitle(Player player) { MessageManager.sendTitle(player, reference, replacement); }
}
