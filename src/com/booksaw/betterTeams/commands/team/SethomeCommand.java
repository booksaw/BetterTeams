package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class SethomeCommand extends TeamSubCommand {

	@Override
	public String onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (player.getRank() == PlayerRank.DEFAULT) {
			return "sethome.noPerm";
		}

		team.setTeamHome(player.getPlayer().getPlayer().getLocation());

		return "sethome.success";

	}

	@Override
	public String getCommand() {
		return "sethome";
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
		return "Sets your team's home";
	}

	@Override
	public String getArguments() {
		return "";
	}

}
