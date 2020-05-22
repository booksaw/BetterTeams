package com.booksaw.betterTeams.commands.team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class InviteCommand extends TeamSubCommand {

	@Override
	public String onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

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

		int limit = Main.plugin.getConfig().getInt("teamLimit");

		if (limit > 0 && limit <= team.getMembers().size()) {
			return "invite.full";
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
	public String getNode() {
		return "invite";
	}

	@Override
	public String getHelp() {
		return "Invite the specified player to your team";
	}

	@Override
	public String getArguments() {
		return "<player>";
	}

}
