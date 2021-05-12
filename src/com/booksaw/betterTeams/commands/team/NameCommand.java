package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class NameCommand extends TeamSubCommand {

    public NameCommand() {
        checkRank = false;
    }

    @Override
    public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

        if (args.length == 0) {
            return new CommandResponse(new ReferencedFormatMessage("name.view", team.getName()));
        }

        if (teamPlayer.getRank().value < getRequiredRank().value) {
            MessageManager.sendMessageF(teamPlayer.getPlayer().getPlayer(), "name.view", team.getName());
            return new CommandResponse("name.noPerm");
        }

        if (!Team.isValidTeamName(args[0])) {
            return new CommandResponse("create.banned");
        }

        int max = Main.plugin.getConfig().getInt("maxTeamLength");
        if (max > 55) {
            max = 55;
        }
        if (max != -1 && max < args[0].length()) {
            return new CommandResponse("create.maxLength");
        }

        if (Team.getTeam(args[0]) != null) {
            return new CommandResponse("name.exists");
        }
        team.setName(args[0]);

        return new CommandResponse(true, "name.success");
    }

    @Override
    public String getCommand() {
        return "name";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public String getNode() {
        return "name";
    }

    @Override
    public String getHelp() {
        return "View and change your team's name";
    }

    @Override
    public String getArguments() {
        return "<name>";
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
        options.add("<name>");
    }

    @Override
    public PlayerRank getDefaultRank() {
        return PlayerRank.OWNER;
    }

}
