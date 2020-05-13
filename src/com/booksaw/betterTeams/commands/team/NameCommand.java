package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

public class NameCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		Player p = (Player) sender;
		Team team = Team.getTeam(p);

		if (team == null) {
			return "inTeam";
		}

		if (args.length == 0) {
			MessageManager.sendMessageF(sender, "name.view", team.getName());
			return null;
		}
		TeamPlayer teamPlayer = team.getTeamPlayer(p);

		if (teamPlayer.getRank() != PlayerRank.OWNER) {
			MessageManager.sendMessageF(sender, "name.view", team.getName());
			return "needOwner";
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
	public boolean needPlayer() {
		return true;
	}

}
