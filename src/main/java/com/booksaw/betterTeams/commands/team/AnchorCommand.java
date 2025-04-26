package com.booksaw.betterTeams.commands.team;

import java.util.List;
import org.bukkit.command.CommandSender;
import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

/**
 * This class handles the '/team anchor' command
 * 
 * @author gaboss44
 */
public class AnchorCommand extends TeamSubCommand {

    @Override
    public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {
        if (args.length == 1) {
            switch(args[0].toLowerCase()) {
                case "true":
                    return setAnchor(team, player, true);
                case "false":
                    return setAnchor(team, player, false);
                default:
                    return new CommandResponse(false, "help.anchor");
            }
        }
        return setAnchor(team, player, !player.isAnchored());
    }

    private CommandResponse setAnchor(Team team, TeamPlayer player, boolean anchor) {
        if (anchor && team.getTeamHome() == null) {
            return new CommandResponse(false, "anchor.noHome");
        }
        team.setPlayerAnchor(player, anchor);
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
        return "Toggle whether you respawn at your team's home";
    }

    @Override
    public String getArguments() {
        return "<true/false>";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            if ("true".startsWith(args[0].toLowerCase()))
                options.add("true");
            if ("false".startsWith(args[0].toLowerCase()))
                options.add("false");
        }
    }
    
}
