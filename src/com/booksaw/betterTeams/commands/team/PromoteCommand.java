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

/**
 * This class handles the command /team promote <player>
 * 
 * @author nfgg2
 *
 */
public class PromoteCommand extends SubCommand {

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

		Team otherTeam = Team.getTeam(player);
		if (team != otherTeam) {
			return "needSameTeam";
		}

		TeamPlayer teamPlayer = team.getTeamPlayer(p);
		TeamPlayer promotePlayer = team.getTeamPlayer(player);

		if (teamPlayer.getRank() != PlayerRank.OWNER) {
			return "promote.noPerm";
		} else if (promotePlayer.getRank() == PlayerRank.OWNER) {
			return "promote.max";

		}

		team.promotePlayer(promotePlayer);
		MessageManager.sendMessasge((CommandSender) promotePlayer.getPlayer(), "promote.notify");

		return "promote.success";

	}

	@Override
	public String getCommand() {
		return "promote";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

}
