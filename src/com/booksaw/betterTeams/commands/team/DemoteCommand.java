package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

/**
 * This class handles the command /team demote <player>
 * 
 * @author nfgg2
 *
 */
public class DemoteCommand extends TeamSubCommand {

	@Override
	public String onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

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

		Team otherTeam = Team.getTeam(player);
		if (team != otherTeam) {
			return "needSameTeam";
		}

		TeamPlayer demotePlayer = team.getTeamPlayer(player);

		if (teamPlayer.getRank() != PlayerRank.OWNER) {
			return "demote.noPerm";
		} else if (demotePlayer.getRank() == PlayerRank.DEFAULT) {
			return "demote.min";

		} else if (demotePlayer.getPlayer().getUniqueId().compareTo(teamPlayer.getPlayer().getUniqueId()) == 0) {
			// trying to demote self
			// checking there is another owner
			if (teamPlayer.getRank() == PlayerRank.OWNER && team.getRank(PlayerRank.OWNER).size() == 1) {
				return "demote.lastOwner";
			}
			// all is good, continue to demotion
		} else if (demotePlayer.getRank() == PlayerRank.OWNER) {
			// the other person is also an owner, players cannot demote other owners
			return "demote.noPerm";
		}

		team.demotePlayer(demotePlayer);
		MessageManager.sendMessage((CommandSender) demotePlayer.getPlayer(), "demote.notify");

		return "demote.success";

	}

	@Override
	public String getCommand() {
		return "promote";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public String getNode() {
		return "demote";
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return "Demote the specified player within your team";
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
