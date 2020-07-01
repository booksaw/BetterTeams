package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;

public class KickCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		/*
		 * method is depreciated as it does not guarantee the expected player, in most
		 * use cases this will work and it will be down to the user if it does not due
		 * to name changes This method is appropriate to use in this use case (so users
		 * can view offline users teams by name not just by team name)
		 */
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

		if (player == null) {
			return new CommandResponse("noPlayer");
		}

		Team otherTeam = Team.getTeam(player);
		if (team != otherTeam) {
			return new CommandResponse("needSameTeam");
		}

		TeamPlayer kickedPlayer = team.getTeamPlayer(player);

		if (teamPlayer.getRank() == PlayerRank.DEFAULT
				|| (teamPlayer.getRank() == PlayerRank.ADMIN && kickedPlayer.getRank() != PlayerRank.DEFAULT)
				|| (teamPlayer.getRank() == PlayerRank.OWNER && kickedPlayer.getRank() == PlayerRank.OWNER)) {
			return new CommandResponse("kick.noPerm");
		}

		team.removePlayer(player);

		MessageManager.sendMessageF((CommandSender) player, "kick.notify", team.getName());

		return new CommandResponse(true, "kick.success");
	}

	@Override
	public String getCommand() {
		return "kick";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public String getNode() {
		return "kick";
	}

	@Override
	public String getHelp() {
		return "Kick that player from your team";
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
