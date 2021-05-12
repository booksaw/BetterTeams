package com.booksaw.betterTeams.commands.teama.chest;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.events.ChestManagement;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChestEnableClaims extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (ChestManagement.enableClaims) {
            return new CommandResponse("admin.chest.enable.already");
        }

        ChestManagement.enableClaims = true;
        Bukkit.broadcastMessage(MessageManager.getPrefix() + MessageManager.getMessage("admin.chest.enabled.bc"));
        return new CommandResponse("admin.chest.enable.success");
    }

    @Override
    public String getCommand() {
        return "enableclaims";
    }

    @Override
    public String getNode() {
        return "admin.chest.enable";
    }

    @Override
    public String getHelp() {
        return "Re-enable Teams chest Claims";
    }

    @Override
    public String getArguments() {
        return "";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public int getMaximumArguments() {
        return 0;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

    }

}
