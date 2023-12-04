package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class NameTeama extends TeamSelectSubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {
		if (!Team.isValidTeamName(args[1])) {
			return new CommandResponse("create.banned");
		}

		int max = Main.plugin.getConfig().getInt("maxTeamLength");
		if (max > 55) {
			max = 55;
		}
		if (max != -1 && max < args[1].length()) {
			return new CommandResponse("create.maxLength");
		}

		if (Team.getTeam(args[1]) != null) {
			return new CommandResponse("name.exists");
		}

		team.setName(args[1]);

		return new CommandResponse(true, "admin.name.success");
	}

	@Override
	public String getCommand() {
		return "name";
	}

	@Override
	public String getNode() {
		return "admin.name";
	}

	@Override
	public String getHelp() {
		return "Set the name for that team";
	}

	@Override
	public String getArguments() {
		return "<team> <name>";
	}

	@Override
	public int getMinimumArguments() {
		return 2;
	}

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		}
	}

}
