package com.booksaw.betterTeams.commands.presets;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;

public abstract class ScoreSubCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Team team = Team.getTeam(args[0]);
		if (team == null) {
			team = Team.getTeam(Bukkit.getPlayer(args[0]));
			if (team == null) {
				return new CommandResponse("noTeam");
			}
		}
		// team is not null
		// getting the score change

		int score = -1;
		try {
			score = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			return new CommandResponse("help");
		}

		if (score < 0) {
			return new CommandResponse("admin.score.tooSmall");
		}

		return onCommand(sender, team, score);
	}

	public abstract CommandResponse onCommand(CommandSender sender, Team team, int change);

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public int getMinimumArguments() {
		return 2;
	}

}
