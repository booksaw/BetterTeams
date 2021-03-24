package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
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
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		if (teamPlayer.getRank() == PlayerRank.OWNER && team.getRank(PlayerRank.OWNER).size() == 1) {
			return new CommandResponse("leave.lastOwner");
		}

		if (team.removePlayer(teamPlayer.getPlayer())) {
			return new CommandResponse(true, "leave.success");
		}
		// event has been cancelled
		return new CommandResponse(false);
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

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

	}

}
