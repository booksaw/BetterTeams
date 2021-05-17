package com.booksaw.betterTeams.commands.teama.chest;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChestRemoveallTeama extends TeamSelectSubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {

		team.clearClaims();

		return new CommandResponse(true, "admin.chest.all.success");
	}

	@Override
	public String getCommand() {
		return "removeall";
	}

	@Override
	public String getNode() {
		return "admin.chest.removeall";
	}

	@Override
	public String getHelp() {
		return "Remove all chest claims from the given team";
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
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		}

	}

}
