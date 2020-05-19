package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

/**
 * This class handles the command /team info <team/player>
 * 
 * @author booksaw
 *
 */
public class InfoCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				return "help";
			}
			Team team = Team.getTeam((Player) sender);
			if (team == null) {
				return "help";
			}
			displayTeamInfo(sender, team);
			return null;
		}

		// player or team has been entered
		// trying by team name
		Team team = Team.getTeam(args[0]);

		if (team != null) {
			displayTeamInfo(sender, team);
			return null;
		}

		// trying by player name
		/*
		 * method is depreciated as it does not guarantee the expected player, in most
		 * use cases this will work and it will be down to the user if it does not due
		 * to name changes This method is appropriate to use in this use case (so users
		 * can view offline users teams by name not just by team name)
		 */
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

		if (player != null) {
			team = Team.getTeam(player);
			if (team != null) {
				displayTeamInfo(sender, team);
				return null;
			}
			return "info.needTeam";
		}

		return "info.fail";
	}

	private void displayTeamInfo(CommandSender sender, Team team) {
		MessageManager.sendMessageF(sender, "info.name", team.getName());
		if (team.getDescription() != null && !team.getDescription().equals("")) {
			MessageManager.sendMessageF(sender, "info.description", team.getDescription());
		}

		MessageManager.sendMessageF(sender, "info.open", team.isOpen() + "");

		List<TeamPlayer> owners = team.getRank(PlayerRank.OWNER);

		if (owners.size() > 0) {
			String ownerStr = "";
			for (TeamPlayer player : owners) {
				ownerStr = ownerStr + player.getPlayer().getName() + " ";
			}

			MessageManager.sendMessageF(sender, "info.owner", ownerStr);
		}

		List<TeamPlayer> admins = team.getRank(PlayerRank.ADMIN);

		if (admins.size() > 0) {
			String adminStr = "";
			for (TeamPlayer player : admins) {
				adminStr = adminStr + player.getPlayer().getName() + " ";
			}

			MessageManager.sendMessageF(sender, "info.admin", adminStr);
		}

		List<TeamPlayer> users = team.getRank(PlayerRank.DEFAULT);

		if (users.size() > 0) {
			String userStr = "";
			for (TeamPlayer player : users) {
				userStr = userStr + player.getPlayer().getName() + " ";
			}

			MessageManager.sendMessageF(sender, "info.default", userStr);
		}

	}

	@Override
	public String getCommand() {
		return "info";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

}
