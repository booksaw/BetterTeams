package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class NeutralCommand extends TeamSubCommand {

    @Override
    public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

        // getting the referenced team
        Team toNeutral = Team.getTeam(args[0]);
        if (toNeutral == null) {
            return new CommandResponse("noTeam");
        } else if (toNeutral == team) {
            return new CommandResponse("neutral.self");
        }

        // if there is an ally request
        if (team.hasRequested(toNeutral.getID())) {
            // removing the ally request
            team.removeAllyRequest(toNeutral.getID());

            // notifying the other team
            for (TeamPlayer p : toNeutral.getMembers()) {
                OfflinePlayer pl = p.getPlayer();
                if (pl.isOnline() && p.getRank() == PlayerRank.OWNER) {
                    MessageManager.sendMessageF(pl.getPlayer(), "neutral.reject", team.getDisplayName());
                }
            }

            return new CommandResponse(true, "neutral.requestremove");
        }

        // if they are allies
        if (toNeutral.isAlly(team.getID())) {
            toNeutral.removeAlly(team.getID());
            team.removeAlly(toNeutral.getID());

            // notifying both teams
            for (TeamPlayer p : toNeutral.getMembers()) {
                OfflinePlayer pl = p.getPlayer();
                if (pl.isOnline()) {
                    MessageManager.sendMessageF(pl.getPlayer(), "neutral.remove", team.getDisplayName());
                }
            }

            // notifying the other team
            for (TeamPlayer p : team.getMembers()) {
                OfflinePlayer pl = p.getPlayer();
                if (pl.isOnline()) {
                    MessageManager.sendMessageF(pl.getPlayer(), "neutral.remove", toNeutral.getDisplayName());
                }
            }
            return new CommandResponse(true);
        }

        return new CommandResponse("neutral.notAlly");
    }

    @Override
    public String getCommand() {
        return "neutral";
    }

    @Override
    public String getNode() {
        return "neutral";
    }

    @Override
    public String getHelp() {
        return "Remove allies and reject ally requests";
    }

    @Override
    public String getArguments() {
        return "<team>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 0;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            addTeamStringList(options, args[0]);
        }
    }

    @Override
    public PlayerRank getDefaultRank() {
        return PlayerRank.OWNER;
    }

}
