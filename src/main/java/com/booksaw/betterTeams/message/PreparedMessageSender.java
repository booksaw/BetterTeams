package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

abstract class PreparedMessageSender<T> implements MessageSender {

    protected abstract @NotNull T prepareMessage(@NotNull String toPrepare);

    protected abstract void sendPreparedMessage(@NotNull CommandSender recipient, @NotNull T preparedMsg);

    protected abstract void sendPreparedTitle(@NotNull Player recipient, @NotNull T preparedMsg);

    @Override
    public final void sendFullMessage(@NotNull CommandSender recipient, @NotNull String message) {
        sendPreparedMessage(recipient, prepareMessage(message));
    }

    @Override
    public final void sendFullMessage(@NotNull Collection<? extends CommandSender> recipients, @NotNull String message) {
        T preparedMsg = prepareMessage(message);
        recipients.stream()
              .forEach(recipient -> sendPreparedMessage(recipient, preparedMsg));
    }

    @Override
    public final void sendFullTitle(@NotNull Player recipient, @NotNull String message) {
        sendPreparedTitle(recipient, prepareMessage(message));
    }

    @Override
    public final void sendFullTitle(@NotNull Collection<? extends Player> recipients, @NotNull String message) {
        T preparedMsg = prepareMessage(message);
        recipients.stream()
              .forEach(recipient -> sendPreparedTitle(recipient, preparedMsg));
    }

}
