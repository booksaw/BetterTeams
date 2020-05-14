package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

public class SethomeCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		Team team = Team.getTeam(p);

		if (team == null) {
			return "inTeam";
		}

		TeamPlayer player = team.getTeamPlayer(p);

		if (player.getRank() == PlayerRank.DEFAULT) {
			return "sethome.noPerm";
		}

		team.setTeamHome(p.getLocation());

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
	public boolean needPlayer() {
		return true;
	}

}
