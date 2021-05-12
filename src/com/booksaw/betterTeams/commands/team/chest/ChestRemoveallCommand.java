package com.booksaw.betterTeams.commands.team.chest;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChestRemoveallCommand extends TeamSubCommand {

    @Override
    public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

        if (player.getRank() != PlayerRank.OWNER) {
            return new CommandResponse("needOwner");
        }

        team.clearClaims();

        return new CommandResponse(true, "chest.all.success");
    }

    @Override
    public String getCommand() {
        return "removeall";
    }

    @Override
    public String getNode() {
        return "chest.removeall";
    }

    @Override
    public String getHelp() {
        return "Remove all claimed chests (useful if you lose track of a chest)";
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
        // no args are required so no tab completion is required
    }

    @Override
    public PlayerRank getDefaultRank() {
        return PlayerRank.OWNER;
    }

}
