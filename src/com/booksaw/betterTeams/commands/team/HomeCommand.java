package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

public class HomeCommand extends TeamSubCommand {

    @Override
    public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

        if (team.getTeamHome() != null) {
            try {
                new PlayerTeleport(Objects.requireNonNull(player.getPlayer().getPlayer()), team.getTeamHome(), "home.success");
            } catch (Exception e) {
                return new CommandResponse("home.world");
            }
            return new CommandResponse(true);
        }

        return new CommandResponse("home.noHome");
    }

    @Override
    public String getCommand() {
        return "home";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public String getNode() {
        return "home";
    }

    @Override
    public String getHelp() {
        return "Teleports you to your team's home";
    }

    @Override
    public String getArguments() {
        return "";
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
