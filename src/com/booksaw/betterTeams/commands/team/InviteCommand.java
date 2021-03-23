package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;

public class InviteCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		if (teamPlayer.getRank() == PlayerRank.DEFAULT) {
			return new CommandResponse("needAdmin");
		}

		Player toInvite = Bukkit.getPlayer(args[0]);

		if (toInvite == null) {
			return new CommandResponse("noPlayer");
		}

		if (team.isBanned(toInvite)) {
			return new CommandResponse("invite.banned");
		}

		if (Team.getTeam(toInvite) != null) {
			return new CommandResponse("invite.inTeam");
		}

		int limit = Main.plugin.getConfig().getInt("levels.l" + team.getLevel() + ".teamLimit");

		if (limit > 0 && limit <= team.getMembers().size() + team.getInvitedPlayers().size()) {
			return new CommandResponse("invite.full");
		}

		// player being invited is not in a team
		team.invite(toInvite.getUniqueId());
		MessageManager.sendMessageF(toInvite, "invite.invite", team.getName());
		return new CommandResponse(true, "invite.success");
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

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		addPlayerStringList(options, (args.length == 0) ? "" : args[0]);
	}

}
