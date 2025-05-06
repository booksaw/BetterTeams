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
		recipients.forEach(recipient -> recipient.sendTitle(s, "", fadeIn, stay, fadeOut));
	}

	@Override
	public void sendSubTitle(@NotNull Player recipient, @NotNull Component subtitle) {
        recipient.sendTitle("", LegacyTextUtils.fromAdventure(subtitle), fadeIn, stay, fadeOut);
	}

	@Override
	public void sendSubTitle(@NotNull Collection<Player> recipients, @NotNull Component subtitle) {
		String s = LegacyTextUtils.fromAdventure(subtitle);
		recipients.forEach(recipient -> recipient.sendTitle("", s, fadeIn, stay, fadeOut));
	}

	@Override
	public void sendTitleAndSub(@NotNull Player recipient, @NotNull Component title, @NotNull Component subtitle) {
        recipient.sendTitle(LegacyTextUtils.fromAdventure(title), LegacyTextUtils.fromAdventure(subtitle), fadeIn, stay, fadeOut);
	}

	@Override
	public void sendTitleAndSub(@NotNull Collection<Player> recipients, @NotNull Component title, @NotNull Component subtitle) {
		String s1 = LegacyTextUtils.fromAdventure(title);
		String s2 = LegacyTextUtils.fromAdventure(subtitle);
		recipients.forEach(recipient -> recipient.sendTitle(s1, s2, fadeIn, stay, fadeOut));
	}
}
