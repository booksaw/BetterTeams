package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaticReferenceMessage extends StaticComponentHolderMessage {

	public StaticReferenceMessage(String reference) {
		super(MessageManager.getMessage(reference));
	}

	@Override
	public void sendMessage(CommandSender recipient) {
		MessageManager.sendFullMessage(recipient, message);
	}

	@Override
	public void sendMessage(Collection<? extends CommandSender> recipients) {
		MessageManager.sendFullMessage(recipients, message);
	}

	@Override
	public void sendTitle(Player recipient) {
		MessageManager.sendFullTitle(recipient, message);
	}

	@Override
	public void sendTitle(Collection<Player> recipients) {
		MessageManager.sendFullTitle(recipients, message);
	}

}
