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
	 * Used to send the message.
	 *
	 * @param recipient the {@link CommandSender} to send the message to.
	 */
	void sendMessage(CommandSender recipient);

	/**
	 * Used to send the message to a {@link Collection} of {@link Player}s.
	 *
	 * @param recipients the {@link CommandSender}s to send the message to
	 */
	void sendMessage(Collection<? extends CommandSender> recipients);

	/**
	 * Used to send a title to a {@link Player}.
	 * <p>
	 * A 'title' is a big text, centered on the recipient screen.
	 * It fades in and out and is only shortly visible.
	 * <p>
	 * This message is not logged in the chat. if you need this, use {@link #sendMessage(CommandSender)} instead.
	 *
	 * @param recipient the {@link Player} to send the title to.
	 * @since 4.9.5
	 */
	void sendTitle(Player recipient);

	/**
	 * Used to send a title to a {@link Collection} of {@link Player}s.
	 * <p>
	 * A 'title' is a big text, centered on the recipient screen.
	 * It fades in and out and is only shortly visible.
	 * <p>
	 * This message is not logged in the chat. if you need this, use {@link #sendMessage(Collection)} instead.
	 * @param recipients the {@link Player}s to send the title to.
	 */
	void sendTitle(Collection<Player> recipients);
	
}
