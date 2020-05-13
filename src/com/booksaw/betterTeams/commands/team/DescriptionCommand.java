package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

public class DescriptionCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		Player p = (Player) sender;
		Team team = Team.getTeam(p);

		if (team == null) {
			return "inTeam";
		}

		if (args.length == 0) {
			if (team.getDescription() != null && !team.getDescription().equals("")) {
				MessageManager.sendMessageF(sender, "description.view", team.getDescription());
			} else {
				MessageManager.sendMessasge(sender, "description.noDesc");
			}

			return null;
		}
		TeamPlayer teamPlayer = team.getTeamPlayer(p);

		if (teamPlayer.getRank() != PlayerRank.OWNER) {
			if (team.getDescription() != null && !team.getDescription().equals("")) {
				MessageManager.sendMessageF(sender, "description.view", team.getDescription());
			} else {
				MessageManager.sendMessasge(sender, "description.noDesc");
			}
			return "needOwner";
		}

		String newDescrip = "";
		for (String temp : args) {
			newDescrip = newDescrip + temp + " ";
		}

		team.setDescription(newDescrip);

		return "description.success";
	}

	@Override
	public String getCommand() {
		return "description";
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
