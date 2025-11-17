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
        recipient.sendMessage(LegacyTextUtils.serialize(message));
    }

	@Override
	public void sendMessage(@NotNull Collection<? extends CommandSender> recipients, @NotNull Component message) {
		String s = LegacyTextUtils.serialize(message);
		recipients.forEach(recipient -> recipient.sendMessage(s));
	}

    @Override
    public void sendTitle(@NotNull Player recipient, @NotNull Component message) {
        recipient.sendTitle(LegacyTextUtils.serialize(message), "", fadeIn, stay, fadeOut);
    }
	@Override
	public void sendTitle(@NotNull Collection<Player> recipients, @NotNull Component message) {
		String s = LegacyTextUtils.serialize(message);
		recipients.forEach(recipient -> recipient.sendTitle(s, "", fadeIn, stay, fadeOut));
	}

	@Override
	public void sendSubTitle(@NotNull Player recipient, @NotNull Component subtitle) {
        recipient.sendTitle("", LegacyTextUtils.serialize(subtitle), fadeIn, stay, fadeOut);
	}

	@Override
	public void sendSubTitle(@NotNull Collection<Player> recipients, @NotNull Component subtitle) {
		String s = LegacyTextUtils.serialize(subtitle);
		recipients.forEach(recipient -> recipient.sendTitle("", s, fadeIn, stay, fadeOut));
	}

	@Override
	public void sendTitleAndSub(@NotNull Player recipient, @NotNull Component title, @NotNull Component subtitle) {
        recipient.sendTitle(LegacyTextUtils.serialize(title), LegacyTextUtils.serialize(subtitle), fadeIn, stay, fadeOut);
	}

	@Override
	public void sendTitleAndSub(@NotNull Collection<Player> recipients, @NotNull Component title, @NotNull Component subtitle) {
		String s1 = LegacyTextUtils.serialize(title);
		String s2 = LegacyTextUtils.serialize(subtitle);
		recipients.forEach(recipient -> recipient.sendTitle(s1, s2, fadeIn, stay, fadeOut));
	}
}
