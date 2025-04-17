package com.booksaw.betterTeams.message;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Used when the program needs to send multiple messages to the user
 *
 * @author booksaw
 */
public class CompositeMessage implements Message {

	final List<Message> messages;

	public CompositeMessage(Message... messages) {
		this.messages = Arrays.asList(messages);
	}

	public CompositeMessage(List<Message> messages) {
		this.messages = messages;
	}

	@Override
	public void sendMessage(CommandSender sender) {
		messages.forEach(message -> message.sendMessage(sender));
	}

	@Override
	public void sendTitle(Player player) {
		messages.forEach(message -> message.sendTitle(player));
	}

	@Override
	public void sendMessage(Collection<? extends CommandSender> senders) {
		messages.forEach(message -> message.sendMessage(senders));
	}

	@Override
	public void sendMessage(Collection<? extends CommandSender> senders, Player player) {
		messages.forEach(message -> message.sendMessage(senders, player));
	}

	@Override
	public void sendTitle(Collection<? extends Player> players) {
		messages.forEach(message -> message.sendTitle(players));
	}

	@Override
	public void sendTitle(Collection<? extends Player> players, Player player) {
		messages.forEach(message -> message.sendTitle(players, player));
	}
}
