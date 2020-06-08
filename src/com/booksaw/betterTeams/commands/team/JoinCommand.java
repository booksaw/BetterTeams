package com.booksaw.betterTeams.commands.team;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.CommandSender;
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

	@Override
	public boolean needPlayer() {
		return true;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		for (Entry<UUID, Team> team : Team.getTeamList().entrySet()) {
			if ((team.getValue().isOpen() || team.getValue().isInvited(((Player) sender).getUniqueId()))
					&& team.getValue().getName().startsWith(args[0])) {
				options.add(team.getValue().getName());
			}
		}
	}

}
