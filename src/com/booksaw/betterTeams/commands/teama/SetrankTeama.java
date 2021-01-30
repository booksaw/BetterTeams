package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import com.booksaw.betterTeams.message.HelpMessage;

public class SetrankTeama extends TeamSelectSubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {

		int level = 0;
		try {
			level = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			level = 0;
			return new CommandResponse(new HelpMessage(this, label));
		}

		if (level <= 0) {
			return new CommandResponse(new HelpMessage(this, label));
		}

		String price = Main.plugin.getConfig().getString("levels.l" + level + ".price");
		if (level > 1 && (price == null || price.equals(""))) {
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
