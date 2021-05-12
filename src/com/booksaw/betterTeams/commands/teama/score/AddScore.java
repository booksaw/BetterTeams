package com.booksaw.betterTeams.commands.teama.score;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.ScoreSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AddScore extends ScoreSubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, Team team, int change) {

        team.setScore(team.getScore() + change);

        return new CommandResponse("admin.score.success");
    }

    @Override
    public String getCommand() {
        return "add";
    }

    @Override
    public String getNode() {
        return "admin.score.add";
    }

    @Override
    public String getHelp() {
        return "Add the specified amount to that players score";
    }

    @Override
    public String getArguments() {
        return "<player/team> <score>";
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            addTeamStringList(options, args[0]);
            addPlayerStringList(options, args[0]);
        } else if (args.length == 2) {
            options.add("<score>");
        }
    }

}
