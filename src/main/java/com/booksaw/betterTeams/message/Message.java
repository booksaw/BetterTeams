package com.booksaw.betterTeams.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class is used to handle the multiple types of message which the plugin
 * contains
 *
 * @author booksaw
 * @since 3.1.0
 */
public interface Message {

	/**
	 * Used to send the message
	 *
	 * @param sender the player to send the message to
	 */
	void sendMessage(CommandSender sender);

	/**
	 * Used to send a title to a player.
	 * A 'title' is a big text, centered on the player screen.
	 *   It fades in and out and is only shortly visible.
	 *   This message is not logged in the chat. if you need this, use {@link #sendMessage(CommandSender)} instead.
	 *
	 * @param player the player to send the title to
	 * @since 4.9.5
	 */
	void sendTitle(Player player);

}
