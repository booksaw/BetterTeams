package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

public class LeaveCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		Team team = Team.getTeam(p);

		if (team == null) {
			return "leave.inTeam";
		}

		TeamPlayer teamPlayer = team.getTeamPlayer(p);

		if (teamPlayer.getRank() == PlayerRank.OWNER && team.getRank(PlayerRank.OWNER).size() == 1) {
			return "leave.lastOwner";
		}

		team.removePlayer(p);

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
	public boolean needPlayer() {
		return true;
	}

}
