package com.booksaw.betterTeams.commands.team;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

public class InviteCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		Player p = (Player) sender;
		Team team = Team.getTeam(p);

		if (team == null) {
			return "inTeam";
		}

		TeamPlayer teamPlayer = team.getTeamPlayer(p);

		if (teamPlayer.getRank() == PlayerRank.DEFAULT) {
			return "needAdmin";
		}

		Player toInvite = Bukkit.getPlayer(args[0]);

		if (toInvite == null) {
			return "noPlayer";
		}

		if (team.isBanned(toInvite)) {
			return "invite.banned";
		}

		if (Team.getTeam(toInvite) != null) {
			return "invite.inTeam";
		}

		// player being invited is not in a team
		team.invite(toInvite.getUniqueId());
		MessageManager.sendMessageF(toInvite, "invite.invite", team.getName());
		return "invite.success";
	}

	@Override
	public String getCommand() {
		return "invite";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public boolean needPlayer() {
		return true;
	}

}
