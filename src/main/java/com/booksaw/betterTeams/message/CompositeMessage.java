package com.booksaw.betterTeams.message;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Used when the program needs to send multiple messages to the user
 *
 * @author booksaw
 */
public class CompositeMessage implements Message {

	private final List<Message> messages;

	public List<Message> getMessages() {
		return Collections.unmodifiableList(messages);
	}

	public CompositeMessage(Message... messages) {
		this(Arrays.asList(messages));
	}

	public CompositeMessage(List<Message> messages) {
		this.messages = messages;
	}

	@Override
	public void sendMessage(CommandSender recipient) {
		messages.forEach(message -> message.sendMessage(recipient));
	}

	@Override
	public void sendMessage(Collection<? extends CommandSender> recipients) {
		messages.forEach(message -> message.sendMessage(recipients));
	}

	@Override
	public void sendTitle(Player recipient) {
		messages.forEach(message -> message.sendTitle(recipient));
	}

	@Override
	public void sendTitle(Collection<Player> recipients) {
		messages.forEach(message -> message.sendTitle(recipients));
	}
}
