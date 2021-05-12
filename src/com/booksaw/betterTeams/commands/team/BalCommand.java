package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BalCommand extends TeamSubCommand {

    @Override
    public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {
        return new CommandResponse(true, new ReferencedFormatMessage("info.money", team.getBalance()));
    }

    @Override
    public String getCommand() {
        return "bal";
    }

    @Override
    public String getNode() {
        return "balance";
    }

    @Override
    public String getHelp() {
        return "View your teams balance";
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

    @Override
    public PlayerRank getDefaultRank() {
        return PlayerRank.DEFAULT;
    }

}
