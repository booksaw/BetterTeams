package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

/**
 * This class handles the /team leave command
 * 
 * @author booksaw
 *
 */
public class LeaveCommand extends TeamSubCommand {

	@Override
	public String onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		if (teamPlayer.getRank() == PlayerRank.OWNER && team.getRank(PlayerRank.OWNER).size() == 1) {
			return "leave.lastOwner";
		}

		team.removePlayer(teamPlayer.getPlayer());

		return "leave.success";
	}

	@Override
	public String getCommand() {
		return "leave";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "leave";
	}

	@Override
	public String getHelp() {
		return "Leave your current team";
	}

	@Override
	public String getArguments() {
		return "";
	}

}
