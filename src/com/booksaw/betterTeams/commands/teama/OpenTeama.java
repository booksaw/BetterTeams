package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;

public class OpenTeama extends TeamSelectSubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {
		team.setOpen(!team.isOpen());

		if (team.isOpen()) {
			return new CommandResponse(true, "admin.open.successopen");
		}
		return new CommandResponse(true, "admin.open.successclose");
	}

	@Override
	public String getCommand() {
		return "open";
	}

	@Override
	public String getNode() {
		return "admin.open";
	}

	@Override
	public String getHelp() {
		return "Toggle if a team is open or not";
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
