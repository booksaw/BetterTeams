package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class SetAnchorCommand extends TeamSubCommand {

    public SetAnchorCommand() {
        checkRank = false;
    }

    @Override
    public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {
        if (args.length == 0) {
            return new CommandResponse(new ReferencedFormatMessage("info.anchor", team.isAnchored()));
        }

        if (player.getRank().value < getRequiredRank().value) {
            MessageManager.sendMessage(player.getPlayer().getPlayer(), "info.anchor", team.isAnchored());
            return new CommandResponse("setanchor.noPerm");
        }

        if (!"true".equalsIgnoreCase(args[0]) && !"false".equalsIgnoreCase(args[0]))
            return new CommandResponse("setanchor.noBool");

        if (!Boolean.parseBoolean(args[0])) {
            if (!team.isAnchored()) {
                return new CommandResponse(new ReferencedFormatMessage("info.anchor", team.isAnchored()));
            }
            team.setAnchored(false);
            for (Player onlinePlayer : team.getOnlineMembers()) {
                if (onlinePlayer.hasPermission("betterteams.setanchor.notify")) {
                    MessageManager.sendMessage(onlinePlayer, "setanchor.notifyDisable", player.getPlayer().getPlayer().getDisplayName());
                }
            }
            return new CommandResponse(true, "setanchor.disabled");
        }

        if (team.getTeamHome() == null) {
            MessageManager.sendMessage(player.getPlayer().getPlayer(), "info.anchor", team.isAnchored());
            return new CommandResponse("setanchor.noHome");
        } else if (team.isAnchored())
            return new CommandResponse(new ReferencedFormatMessage("info.anchor", team.isAnchored()));

        team.setAnchored(true);
        for (Player onlinePlayer : team.getOnlineMembers()) {
            if (onlinePlayer.hasPermission("betterteams.setanchor.notify")) {
                MessageManager.sendMessage(onlinePlayer, "setanchor.notifyEnable", player.getPlayer().getPlayer().getDisplayName());
            }
        }
        return new CommandResponse(true, "setanchor.enabled");
    }

    @Override
    public PlayerRank getDefaultRank() {
        return PlayerRank.ADMIN;
    }

    @Override
    public String getCommand() {
        return "setanchor";
    }

    @Override
    public String getNode() {
        return "setanchor";
    }

    @Override
    public String getHelp() {
        return "Set your team's anchor state";
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
            if ("true".startsWith(args[0]))
                options.add("true");
            if ("false".startsWith(args[0]))
                options.add("false");
        }
    }

}
