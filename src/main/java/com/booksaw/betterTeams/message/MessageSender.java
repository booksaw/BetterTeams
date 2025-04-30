package com.booksaw.betterTeams.message;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

interface MessageSender {

    void sendMessage(@NotNull CommandSender recipient, @NotNull Component message);

    void sendMessage(@NotNull Collection<? extends CommandSender> recipients, @NotNull Component message);

    void sendTitle(@NotNull Player recipient, @NotNull Component message);

    void sendTitle(@NotNull Collection<Player> recipients, @NotNull Component message);
    
}
