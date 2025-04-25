package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

final class AdventureMessageSender extends PreparedMessageSender<Component> {

    private BukkitAudiences audiences;

    AdventureMessageSender(BukkitAudiences audiences) {
        if (audiences == null) {
            throw new IllegalArgumentException("BukkitAudiences instance cannot be null when initializing AdventureMessageSender");
        }
        this.audiences = audiences;
    }

    void closeAdventure() {
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
    }

    void sendFullMessage(@NotNull CommandSender recipient, @NotNull Component message) {
        sendPreparedMessage(recipient, message);
    }

    void sendFullMessage(@NotNull Collection<? extends CommandSender> recipients, @NotNull Component message) {
        recipients.stream()
              .forEach(recipient -> sendPreparedMessage(recipient, message));
    }

    void sendFullTitle(@NotNull Player recipient, @NotNull Component message) {
        sendPreparedTitle(recipient, message);
    }

    void sendFullTitle(@NotNull Collection<? extends Player> recipients, @NotNull Component message) {
        recipients.stream()
              .forEach(recipient -> sendPreparedTitle(recipient, message));
    }

    @Override
    protected @NotNull Component prepareMessage(@NotNull String toPrepare) {
        return Formatter.absoluteMinimessage(toPrepare);
    }

    @Override
    protected void sendPreparedMessage(@NotNull CommandSender recipient, @NotNull Component preparedMsg) {
        if (recipient instanceof Player) {
            this.audiences.player((Player) recipient).sendMessage(preparedMsg);
        } else if (recipient instanceof ConsoleCommandSender) {
            this.audiences.console().sendMessage(preparedMsg);
        }
    }

    @Override
    protected void sendPreparedTitle(@NotNull Player recipient, @NotNull Component preparedMsg) {
        this.audiences.player(recipient).showTitle(Title.title(preparedMsg, Component.empty()));
    }

}
