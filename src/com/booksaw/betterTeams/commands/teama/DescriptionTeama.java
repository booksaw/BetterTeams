package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;

public class DescriptionTeama extends TeamSelectSubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {
		String newDescrip = "";
		int i = 0;
		for (String temp : args) {
			if (i == 0) {
				i++;
				continue;
			}
			newDescrip = newDescrip + temp + " ";
		}

		team.setDescription(newDescrip);

		return new CommandResponse(true, "admin.description.success");
	}

	@Override
	public String getCommand() {
		return "description";
	}

	@Override
	public String getNode() {
		return "admin.description";
	}

	@Override
	public String getHelp() {
		return "Change the specified teams description";
	}

	@Override
	public String getArguments() {
		return "<team> <description>";
	}

	@Override
	public int getMinimumArguments() {
		return 2;
	}

	@Override
	public int getMaximumArguments() {
		return -1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		}
	}

}
