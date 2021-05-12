package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BaltopCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

        Team team = null;
        boolean contained = false;

        if (sender instanceof Player) {
            team = Team.getTeam((Player) sender);
        }

        Team[] teams = Team.sortTeamsByBalance();
        MessageManager.sendMessage(sender, "baltop.leaderboard");

        for (int i = 0; i < 10 && i < teams.length; i++) {
            MessageManager.sendMessageF(sender, "baltop.syntax", (i + 1) + "", teams[i].getName(),
                    teams[i].getBalance() + "");
            if (team == teams[i]) {
                contained = true;
            }
        }

        if (!contained && team != null) {
            try {
                int rank = 0;
                for (int i = 10; i < teams.length; i++) {
                    if (teams[i] == team) {
                        rank = i + 1;
                        break;
                    }
                }
                if (rank != 0) {
                    MessageManager.sendMessage(sender, "baltop.divide");
                    if (rank - 2 > 9) {
                        sender.sendMessage(
                                MessageManager.getPrefix() + String.format(MessageManager.getMessage("baltop.syntax"),
                                        (rank - 1) + "", teams[rank - 2].getName(), teams[rank - 2].getBalance()));
                        MessageManager.sendMessageF(sender, "baltop.syntax", (rank - 1) + "", teams[rank - 2].getName(),
                                teams[rank - 2].getBalance() + "");
                    }

                    MessageManager.sendMessageF(sender, "baltop.syntax", (rank) + "", team.getName(),
                            team.getBalance() + "");

                    if (teams.length > rank) {
                        MessageManager.sendMessageF(sender, "baltop.syntax", (rank + 1) + "", teams[rank].getName(),
                                teams[rank].getBalance() + "");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // to save an additional check on arrays length
            }
        }

        return new CommandResponse(true);
    }

    @Override
    public String getCommand() {
        return "baltop";
    }

    @Override
    public String getNode() {
        return "baltop";
    }

    @Override
    public String getHelp() {
        return "View the richest teams";
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
