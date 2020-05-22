package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class HomeCommand extends TeamSubCommand {

	@Override
	public String onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (team.getTeamHome() != null) {
			player.getPlayer().getPlayer().teleport(team.getTeamHome());
			return "home.success";
		}

		return "home.noHome";
	}

	@Override
	public String getCommand() {
		return "home";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "home";
	}

	@Override
	public String getHelp() {
		return "Teleports you to your team's home";
	}

	@Override
	public String getArguments() {
		return "";
	}

}
