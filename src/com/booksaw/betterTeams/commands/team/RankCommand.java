package com.booksaw.betterTeams.commands.team;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.CompositeMessage;
import com.booksaw.betterTeams.message.ReferenceMessage;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;

public class RankCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Team team = null;

		if (args.length == 0 && sender instanceof Player) {
			team = Team.getTeam((Player) sender);
		} else if (args.length >= 1) {
			team = Team.getTeam(args[0]);
		}

		if (team == null) {
			return new CommandResponse("rank.noTeam");
		}

		Team.sortTeams();
		int rank = team.getTeamRank() + 1;

		return new CommandResponse(true, new CompositeMessage(new ReferenceMessage("rank.info"),
				new ReferencedFormatMessage("rank.syntax", rank + "", team.getScore() + "")));
	}

	@Override
	public String getCommand() {
		return "rank";
	}

	@Override
	public String getNode() {
		return "rank";
	}

	@Override
	public String getHelp() {
		return "View the rank of a team";
	}

	@Override
	public String getArguments() {
		return "[team]";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

		for (Entry<UUID, Team> team : Team.getTeamList().entrySet()) {
			options.add(team.getValue().getName());
		}

	}

}
