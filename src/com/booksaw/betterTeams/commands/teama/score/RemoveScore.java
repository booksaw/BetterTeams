package com.booksaw.betterTeams.commands.teama.score;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.ScoreSubCommand;

public class RemoveScore extends ScoreSubCommand {
	@Override
	public CommandResponse onCommand(CommandSender sender, Team team, int change) {

		team.setScore(Math.max(team.getScore() - change, 0));

		return new CommandResponse("admin.score.success");
	}

	@Override
	public String getCommand() {
		return "remove";
	}

	@Override
	public String getNode() {
		return "admin.score.remove";
	}

	@Override
	public String getHelp() {
		return "Remove the specified amount from that players score";
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
