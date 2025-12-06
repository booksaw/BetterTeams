package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import com.booksaw.betterTeams.message.HelpMessage;
import com.booksaw.betterTeams.team.level.LevelManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetrankTeama extends TeamSelectSubCommand {


	private final ParentCommand parentCommand;

	public SetrankTeama(ParentCommand parentCommand) {
		this.parentCommand = parentCommand;
	}

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {

		int level;
		try {
			level = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			return new CommandResponse(new HelpMessage(this, label, parentCommand));
		}

		if (!LevelManager.exists(level)) {
			return new CommandResponse(true, "admin.setrank.no");
		}

		team.setLevel(level);

		return new CommandResponse(true, "admin.setrank.success");
	}

	@Override
	public String getCommand() {
		return "setrank";
	}

	@Override
	public String getNode() {
		return "admin.setrank";
	}

	@Override
	public String getHelp() {
		return "Set the level of the team specified";
	}

	@Override
	public String getArguments() {
		return "<team> <rank>";
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
		} else if (args.length == 2) {
			options.add("<rank>");
		}
	}

}
