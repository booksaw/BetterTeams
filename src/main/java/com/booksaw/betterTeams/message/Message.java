package com.booksaw.betterTeams.message;

import java.util.Collection;

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
	 * Used to send the message to a collection of players
	 *
	 * @param senders the players to send the message to
	 */
	void sendMessage(Collection<? extends CommandSender> senders);

	/**
	 * Used to send the message, formatted around a single player, 
	 * to a collection of players
	 *
	 * @param senders the players to send the message to
	 * @param player  the player to format the message around
	 */
	void sendMessage(Collection<? extends CommandSender> senders, Player player);

	/**
	 * Used to send a title to a player.
	 * A 'title' is a big text, centered on the player screen.
	 * It fades in and out and is only shortly visible.
	 * This message is not logged in the chat. if you need this, use {@link #sendMessage(CommandSender)} instead.
	 *
	 * @param player the player to send the title to
	 * @since 4.9.5
	 */
	void sendTitle(Player player);

	/**
	 * Used to send a title to a collection of players.
	 * A 'title' is a big text, centered on the player screen.
	 * It fades in and out and is only shortly visible.
	 * @param players the players to send the title to
	 */
	void sendTitle(Collection<? extends Player> players);

	/**
	 * Used to send a title to a collection of players.
	 * A 'title' is a big text, centered on the player screen.
	 * It fades in and out and is only shortly visible.
	 * @param players the players to send the title to
	 * @param player  the player to format the message around
	 */
	void sendTitle(Collection<? extends Player> players, Player player);
	
}
