package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

public class ChatCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		Team team = Team.getTeam(p);

		if (team == null) {
			return "inTeam";
		}

		TeamPlayer teamPlayer = team.getTeamPlayer(p);
		if (args.length == 0) {
			// toggle chat
			if (teamPlayer.isInTeamChat()) {
				teamPlayer.setTeamChat(false);
				return "chat.disabled";
			} else {
				teamPlayer.setTeamChat(true);
				return "chat.enabled";
			}
		}

		String message = "";
		for (int i = 0; i < args.length; i++) {
			message = message + args[i] + " ";
		}

		team.sendMessage(teamPlayer, message);
		return null;
	}

	@Override
	public String getCommand() {
		return "chat";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

}
