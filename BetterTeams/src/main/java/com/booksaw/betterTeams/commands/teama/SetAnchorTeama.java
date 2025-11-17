package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;

public class SetAnchorTeama extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        
        Team team = Team.getTeam(args[0]);
        if (team == null)
            return new CommandResponse(false, "noTeam");

        if (args.length < 2)
            return new CommandResponse(false, new ReferencedFormatMessage("info.anchor", team.isAnchored()));

        switch (args[1]) {
            case "true":
                if (team.isAnchored())
                    return new CommandResponse(false, new ReferencedFormatMessage("info.anchor", team.isAnchored()));
                if (!team.toggleAnchor())
                    return new CommandResponse(false, "admin.setanchor.noHome");
                return new CommandResponse(true, "admin.setanchor.enabled");
            case "false":
                if (!team.isAnchored())
                    return new CommandResponse(false, new ReferencedFormatMessage("info.anchor", team.isAnchored()));
                team.toggleAnchor();
                return new CommandResponse(true, "admin.setanchor.disabled");
            default:
                return new CommandResponse(false, "setanchor.noBool");
        }
    }

    @Override
    public String getCommand() {
        return "setanchor";
    }

    @Override
    public String getNode() {
        return "admin.setanchor";
    }

    @Override
    public String getHelp() {
        return "Set a team's anchor status";
    }

    @Override
    public String getArguments() {
        return "<team> <true/false>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
        if (args.length == 1)
            addTeamStringList(options, args[0]);
        if (args.length == 2) {
            if ("true".startsWith(args[1])) {
                options.add("true");
            }
            if ("false".startsWith(args[1])) {
                options.add("false");
            }
        }
    }

}
