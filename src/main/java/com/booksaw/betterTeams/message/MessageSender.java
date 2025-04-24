package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

interface MessageSender {

    void sendFullMessage(@NotNull CommandSender recipient, @NotNull String message);

    void sendFullMessage(@NotNull Collection<? extends CommandSender> recipients, @NotNull String message);

    void sendFullTitle(@NotNull Player recipient, @NotNull String message);

    void sendFullTitle(@NotNull Collection<? extends Player> recipients, @NotNull String message);
    
}
