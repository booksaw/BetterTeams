package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.Warp;
import com.booksaw.betterTeams.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DelwarpTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Team team = Team.getTeam(args[0]);
		if (team == null) {
			return new CommandResponse("noTeam");
		}

		Warp warp = team.getWarp(args[1]);
		if (warp == null) {
			return new CommandResponse("warp.nowarp");
		}

		team.delWarp(args[1]);

		return new CommandResponse(true, "delwarp.success");
	}

	@Override
	public String getCommand() {
		return "delwarp";
	}

	@Override
	public String getNode() {
		return "admin.delwarp";
	}

	@Override
	public String getHelp() {
		return "Delete that warp from your team";
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
			options.add("<team>");
		}
		if (args.length == 2) {
			options.add("<name>");
		}
	}

}
