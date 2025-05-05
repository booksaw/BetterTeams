package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.booksaw.betterTeams.text.LegacyTextUtils;

import net.kyori.adventure.text.Component;

final class LegacyMessageSender implements MessageSender {

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    LegacyMessageSender() {
        this.fadeIn = 10;
        this.stay = 70;
        this.fadeOut = 20;
    }

    @Override
    public void sendMessage(@NotNull CommandSender recipient, @NotNull Component message) {
        recipient.sendMessage(LegacyTextUtils.fromAdventure(message));
    }

	@Override
	public void sendMessage(@NotNull Collection<? extends CommandSender> recipients, @NotNull Component message) {
		String s = LegacyTextUtils.fromAdventure(message);
		recipients.forEach(recipient -> recipient.sendMessage(s));
	}

    @Override
    public void sendTitle(@NotNull Player recipient, @NotNull Component message) {
        recipient.sendTitle(LegacyTextUtils.fromAdventure(message), "", fadeIn, stay, fadeOut);
    }
	@Override
	public void sendTitle(@NotNull Collection<Player> recipients, @NotNull Component message) {
		String s = LegacyTextUtils.fromAdventure(message);
		recipients.forEach(recipient -> recipient.sendTitle(s, "", 10, 70, 20));
	}

}
