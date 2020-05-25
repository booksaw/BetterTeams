package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class NameCommand extends TeamSubCommand {

	@Override
	public String onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		if (args.length == 0) {
			MessageManager.sendMessageF(teamPlayer.getPlayer().getPlayer(), "name.view", team.getName());
			return null;
		}

		if (teamPlayer.getRank() != PlayerRank.OWNER) {
			MessageManager.sendMessageF(teamPlayer.getPlayer().getPlayer(), "name.view", team.getName());
			return "needOwner";
		}

		for (String temp : Main.plugin.getConfig().getStringList("blacklist")) {
			if (temp.toLowerCase().equals(args[0].toLowerCase())) {
				return "create.banned";
			}
		}
		int max = Main.plugin.getConfig().getInt("maxTeamLength");
		if (max != -1 && max < args[0].length()) {
			return "create.maxLength";
		}

		if (Team.getTeam(args[0]) != null) {
			return "name.exists";
		}
		team.setName(args[0]);

		return "name.success";
	}

	@Override
	public String getCommand() {
		return "name";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "name";
	}

	@Override
	public String getHelp() {
		return "View and change your team's name";
	}

	@Override
	public String getArguments() {
		return "<name>";
	}

}
