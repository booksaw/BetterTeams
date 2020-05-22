package com.booksaw.betterTeams.commands.team;

import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.NoTeamSubCommand;

public class JoinCommand extends NoTeamSubCommand {

	@Override
	public String onCommand(Player p, String label, String[] args) {

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

		int limit = Main.plugin.getConfig().getInt("teamLimit");

		if (limit > 0 && limit <= team.getMembers().size()) {
			return "join.full";
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

	@Override
	public String getNode() {
		return "join";
	}

	@Override
	public String getHelp() {
		return "Join the specified team";
	}

	@Override
	public String getArguments() {
		return "<team>";
	}

}
