package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;

public class HomeCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		Player p = (Player) sender;
		Team team = Team.getTeam(p);

		if (team == null) {
			return "inTeam";
		}

		if (team.getTeamHome() != null) {
			p.teleport(team.getTeamHome());
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
	public boolean needPlayer() {
		return true;
	}

}
