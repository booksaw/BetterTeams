package com.booksaw.betterTeams.commands.team;

import java.util.List;
import org.bukkit.command.CommandSender;
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
        team.setPlayerAnchor(player, !player.isAnchored());
        if (player.isAnchored() && team.getTeamHome() == null)
            MessageManager.sendMessage(player.getPlayer().getPlayer(), "anchor.noHome");
        return player.isAnchored() ? new CommandResponse(true, "anchor.enabled")
                : new CommandResponse(true, "anchor.disabled");
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
