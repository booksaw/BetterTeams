package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class BaltopCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Team teamPre = null;

		if (sender instanceof Player) {
			teamPre = Team.getTeam((Player) sender);
		}

		Team team = teamPre;

		MessageManager.sendMessage(sender, "loading");

		new BukkitRunnable() {

			@Override
			public void run() {
				boolean contained = false;
				String[] teams = Team.getTeamManager().sortTeamsByBalance();
				MessageManager.sendMessage(sender, "baltop.leaderboard");

				for (int i = 0; i < 10 && i < teams.length; i++) {
					Team tempTeam = Team.getTeam(teams[i]);
					MessageManager.sendMessageF(sender, "baltop.syntax", (i + 1) + "", tempTeam.getName(),
							tempTeam.getBalance() + "");
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
							MessageManager.sendMessage(sender, "baltop.divide");
							if (rank - 2 > 9) {
								Team tm2 = Team.getTeam(teams[rank - 2]);
								MessageManager.sendMessageF(sender, "baltop.syntax", (rank - 1) + "", tm2.getName(),
										tm2.getBalance() + "");
							}

							MessageManager.sendMessageF(sender, "baltop.syntax", (rank) + "", team.getName(),
									team.getBalance() + "");

							if (teams.length > rank) {
								Team tm = Team.getTeam(teams[rank]);
								MessageManager.sendMessageF(sender, "baltop.syntax", (rank + 1) + "", tm.getName(),
										tm.getBalance() + "");
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						// to save an additional check on arrays length
					}
				}
			}
		}.runTaskAsynchronously(Main.plugin);

		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "baltop";
	}

	@Override
	public String getNode() {
		return "baltop";
	}

	@Override
	public String getHelp() {
		return "View the richest teams";
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
