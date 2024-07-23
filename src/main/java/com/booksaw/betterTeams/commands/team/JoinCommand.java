package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.NoTeamSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class JoinCommand extends NoTeamSubCommand {

	@Override
	public CommandResponse onCommand(Player p, String label, String[] args) {

		Team team = Team.getTeam(args[0]);
		if (team == null) {
			return new CommandResponse("notTeam");
		}

		if (team.isBanned(p)) {
			return new CommandResponse("join.banned");
		}

		if (!team.isOpen() && !team.isInvited(p.getUniqueId())) {
			return new CommandResponse("join.notInvited");
		}

		int limit = team.getTeamLimit();
		if (limit > 0 && limit <= team.getMembers().size()) {
			return new CommandResponse("join.full");
		}

		if (team.join(p)) {
			return new CommandResponse(true, "join.success");
		}
		// join event was cancelled, whatever the cause of the event should handle
		// notifying the user
		return new CommandResponse(false);
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
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		for (Entry<UUID, Team> team : Team.getTeamManager().getLoadedTeamListClone().entrySet()) {
			if ((team.getValue().isOpen() || team.getValue().isInvited(((Player) sender).getUniqueId()))
					&& team.getValue().getName().startsWith(args[0])) {
				options.add(team.getValue().getName());
			}
		}
	}

}
