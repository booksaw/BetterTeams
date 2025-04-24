package com.booksaw.betterTeams.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class LegacyMessageSender extends PreparedMessageSender<String> {

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    LegacyMessageSender() {
        this.fadeIn = 10;
        this.stay = 70;
        this.fadeOut = 20;
    }
    
    @Override
    protected @NotNull String prepareMessage(@NotNull String toPrepare) {
        return Formatter.legacySerialize(toPrepare);
    }

    @Override
    protected void sendPreparedMessage(@NotNull CommandSender recipient, @NotNull String preparedMsg) {
        recipient.sendMessage(preparedMsg);
    }

    @Override
    protected void sendPreparedTitle(@NotNull Player recipient, @NotNull String preparedMsg) {
        recipient.sendTitle(preparedMsg, "", fadeIn, stay, fadeOut);
    }

}
