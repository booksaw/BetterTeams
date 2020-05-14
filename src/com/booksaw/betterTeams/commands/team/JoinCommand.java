package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;

public class JoinCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		Player p = (Player) sender;

		if (Team.getTeam(p) != null) {
			return "notInTeam";
		}

		Team team = Team.getTeam(args[0]);
		if (team == null) {
			return "notTeam";
		}

		if (team.isBanned(p)) {
			return "join.banned";
		}

		if (!team.isOpen() && !team.isInvited(p.getUniqueId())) {
			return "join.notInvited";
		}

		team.join(p);

		return "join.success";
	}

	@Override
	public String getCommand() {
		return "join";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

}
