package com.booksaw.betterTeams.message;

import org.bukkit.command.CommandSender;

/**
 * Used when the program needs to send multiple messages to the user
 *
 * @author booksaw
 */
public class CompositeMessage implements Message {

    final Message message1;
    final Message message2;

    public CompositeMessage(Message message1, Message message2) {
        this.message1 = message1;
        this.message2 = message2;
    }

    @Override
    public void sendMessage(CommandSender sender) {
        message1.sendMessage(sender);
        message2.sendMessage(sender);
    }
}
