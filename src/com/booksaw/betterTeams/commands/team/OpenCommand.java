package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

/**
 * This handles the command /team open
 * <p>
 * This toggles the team from being open to closed and back
 * </p>
 * 
 * @author booksaw
 *
 */
public class OpenCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		Team team = Team.getTeam(p);

		if (team == null) {
			return "inTeam";
		}

		TeamPlayer teamPlayer = team.getTeamPlayer(p);

		if (teamPlayer.getRank() != PlayerRank.OWNER) {
			return "needOwner";
		}

		team.setOpen(!team.isOpen());

		if (team.isOpen()) {
			return "open.successopen";
		}
		return "open.successclose";

	}

	@Override
	public String getCommand() {
		return "open";
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
