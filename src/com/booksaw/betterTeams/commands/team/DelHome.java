package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class DelHome extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (player.getRank() == PlayerRank.DEFAULT) {
			return new CommandResponse("needAdmin");
		}

		if (team.getTeamHome() == null) {
			return new CommandResponse("delhome.noHome");
		}

		team.deleteTeamHome();

		return new CommandResponse(true, "delhome.success");
	}

	@Override
	public String getCommand() {
		return "delhome";
	}

	@Override
	public String getNode() {
		return "delhome";
	}

	@Override
	public String getHelp() {
		return "Delete your teams home";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}

}
