package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TopCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Team team = null;
		boolean contained = false;

		if (sender instanceof Player) {
			team = Team.getTeam((Player) sender);
		}

		String[] teams = Team.getTeamManager().sortTeamsByScore();
		MessageManager.sendMessage(sender, "top.leaderboard");

		for (int i = 0; i < 10 && i < teams.length; i++) {
			Team tempTeam = Team.getTeam(teams[i]);
			MessageManager.sendMessageF(sender, "top.syntax", (i + 1) + "", tempTeam.getName(),
					tempTeam.getScore() + "");
			if (team == tempTeam) {
				contained = true;
			}
		}

		if (!contained && team != null) {
			try {
				int rank = 0;
				for (int i = 10; i < teams.length; i++) {
					if (teams[i].equals(team.getName())) {
						rank = i + 1;
						break;
					}
				}
				if (rank != 0) {
					MessageManager.sendMessage(sender, "top.divide");
					if (rank - 2 > 9) {
						Team tm2 = Team.getTeam(teams[rank - 2]);
						MessageManager.sendMessageF(sender, "top.syntax", (rank - 1) + "", tm2.getName(),
								tm2.getScore() + "");
					}

					MessageManager.sendMessageF(sender, "top.syntax", (rank) + "", team.getName(),
							team.getScore() + "");

					if (teams.length > rank) {
						Team tm = Team.getTeam(teams[rank]);
						MessageManager.sendMessageF(sender, "top.syntax", (rank + 1) + "", tm.getName(),
								tm.getScore() + "");
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				// to save an additional check on arrays length
			}
		}

		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "top";
	}

	@Override
	public String getNode() {
		return "top";
	}

	@Override
	public String getHelp() {
		return "View the top teams";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}

}
