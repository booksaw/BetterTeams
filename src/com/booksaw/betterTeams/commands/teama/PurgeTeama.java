package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PurgeTeama extends SubCommand {

    long confirm = 0;

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

        if (System.currentTimeMillis() - 10000 > confirm) {
            confirm = System.currentTimeMillis();
            return new CommandResponse("admin.purge.confirm");
        }

        if (Team.purge())
            return new CommandResponse(true, "admin.purge.success");

        return new CommandResponse(false);
    }

    @Override
    public String getCommand() {
        return "purge";
    }

    @Override
    public String getNode() {
        return "admin.purge";
    }

    @Override
    public String getHelp() {
        return "Reset the score for all teams";
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
