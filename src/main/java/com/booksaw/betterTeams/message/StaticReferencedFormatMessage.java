package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class StaticReferencedFormatMessage extends StaticComponentHolderMessage {

	public StaticReferencedFormatMessage(String reference, Object... replacements) {
		super(MessageManager.getMessage(reference, replacements));
	}

	public StaticReferencedFormatMessage(@Nullable OfflinePlayer player, String reference, Object... replacements) {
		super(MessageManager.getMessage(player, reference, replacements));
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
