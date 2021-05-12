package com.booksaw.betterTeams.commands.teama.score;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.ScoreSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetScore extends ScoreSubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, Team team, int change) {

        team.setScore(change);

        return new CommandResponse("admin.score.success");
    }

    @Override
    public String getCommand() {
        return "set";
    }

    @Override
    public String getNode() {
        return "admin.score.set";
    }

    @Override
    public String getHelp() {
        return "Set the specified teams score";
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
