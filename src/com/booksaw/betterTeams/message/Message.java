package com.booksaw.betterTeams.message;

import org.bukkit.command.CommandSender;

/**
 * This class is used to handle the multiple types of message which the plugin
 * contains
 *
 * @author booksaw
 * @since 3.1.0
 */
public interface Message {

    /**
     * Used to send the message
     *
     * @param sender the player to send the message to
     */
    void sendMessage(CommandSender sender);

}
