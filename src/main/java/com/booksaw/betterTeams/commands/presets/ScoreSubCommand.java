package com.booksaw.betterTeams.commands.presets;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import org.bukkit.command.CommandSender;

public abstract class ScoreSubCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Team team;
		try {
			team = Team.getTeam(args[0]);
		} catch (NullPointerException e) {
			return new CommandResponse("noTeam");
		}

		if (team == null) {
			return new CommandResponse("noTeam");
		}
		// team is not null
		// getting the score change

		int score;
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
