package com.booksaw.betterTeams.commands.team;

import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;

/**
 * This class handles the '/team anchor' command
 * 
 * @author gaboss44
 */
public class AnchorCommand extends TeamSubCommand {

    @Override
    public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {
        if(player.isAnchored()){
            team.unanchorPlayer(player);
            return new CommandResponse("anchor.disabled");
        }
        else {
            team.anchorPlayer(player);
            Optional<Player> optSender = player.getOnlinePlayer();
            if(team.getTeamHome() == null && optSender.isPresent()) {
                MessageManager.sendMessage(optSender.get(), "anchor.noHome");
            }
            return new CommandResponse("anchor.enabled");
        }
    }

    @Override
    public PlayerRank getDefaultRank() {
        return PlayerRank.DEFAULT;
    }

    @Override
    public String getCommand() {
        return "anchor";
    }

    @Override
    public String getNode() {
        return "anchor";
    }

    @Override
    public String getHelp() {
        return "Toggle the home anchor state for yourself";
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
