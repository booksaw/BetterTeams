package com.booksaw.betterTeams.commands.team;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

public class BanCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		Player p = (Player) sender;
		Team team = Team.getTeam(p);

		if (team == null) {
			return "inTeam";
		}

		/*
		 * method is depreciated as it does not guarantee the expected player, in most
		 * use cases this will work and it will be down to the user if it does not due
		 * to name changes This method is appropriate to use in this use case (so users
		 * can view offline users teams by name not just by team name)
		 */
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

		if (player == null) {
			return "noPlayer";
		}

		TeamPlayer teamPlayer = team.getTeamPlayer(p);
		Team otherTeam = Team.getTeam(player);

		if (team.isBanned(player)) {
			return "ban.already";
		}

		if (team != otherTeam) {
			if (teamPlayer.getRank() != PlayerRank.DEFAULT) {
				team.banPlayer(player);
				MessageManager.sendMessageF((CommandSender) player, "ban.notify", team.getName());
				return "ban.success";
			}
			return "ban.noPerm";
		}

		TeamPlayer kickedPlayer = team.getTeamPlayer(player);

		if (teamPlayer.getRank() == PlayerRank.DEFAULT
				|| (teamPlayer.getRank() == PlayerRank.ADMIN && kickedPlayer.getRank() != PlayerRank.DEFAULT)
				|| (teamPlayer.getRank() == PlayerRank.OWNER && kickedPlayer.getRank() == PlayerRank.OWNER)) {
			return "ban.noPerm";
		}

		// player is in the team, so removing them
		team.removePlayer(player);
		team.banPlayer(player);
		MessageManager.sendMessageF((CommandSender) player, "ban.notify", team.getName());

		return "ban.success";
	}

	@Override
	public String getCommand() {
		return "ban";
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
