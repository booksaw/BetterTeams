package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

final class AdventureMessageSender implements MessageSender {

	private BukkitAudiences audiences;

    AdventureMessageSender(BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public void sendMessage(@NotNull CommandSender recipient, @NotNull Component message) {
		audiences.sender(recipient).sendMessage(message);
    }

	@Override
	public void sendMessage(@NotNull Collection<? extends CommandSender> recipients, @NotNull Component message) {
		audiences.filter(recipients::contains).sendMessage(message);
	}

    @Override
    public void sendTitle(@NotNull Player recipient, @NotNull Component message) {
        audiences.player(recipient).showTitle(Title.title(message, Component.empty()));
    }

	@Override
	public void sendTitle(@NotNull Collection<Player> recipients, @NotNull Component message) {
		audiences.filter(recipients::contains).showTitle(Title.title(message, Component.empty()));
	}
}
