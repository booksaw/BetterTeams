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

public class TopCommand extends SubCommand {

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
				String[] teams = Team.getTeamManager().sortTeamsByScore();
				MessageManager.sendMessage(sender, "top.leaderboard");

				for (int i = 0; i < 10 && i < teams.length; i++) {
					if (teams[i] == null) {
						Main.plugin.getLogger().severe("Team at position [" + i + "] had a null name");
						continue;
					}
					Team tempTeam = Team.getTeam(teams[i]);
					sendTopSyntaxMessage(sender, i + 1, tempTeam, teams[i]);
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
								sendTopSyntaxMessage(sender, rank - 1, tm2, teams[rank - 2]);
							}

							sendTopSyntaxMessage(sender, rank, team, "CommandSenders Team");

							if (teams.length > rank) {
								Team tm = Team.getTeam(teams[rank]);
								sendTopSyntaxMessage(sender, (rank + 1), tm, teams[rank]);
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

	private void sendTopSyntaxMessage(CommandSender sender, int rank, Team team, String teamIdentifier) {
		if (team == null || team.getName() == null) {
			Main.plugin.getLogger()
					.warning("There is an issue with the team file associated with " + teamIdentifier);
			Main.plugin.getLogger().warning("in config.yml set 'rebuildLookups' to true and restart your server, if the issue continues please report it to booksaw");
			return;
		}

		MessageManager.sendMessage(sender, "top.syntax", rank, team.getName(), team.getScore());

	}

}
