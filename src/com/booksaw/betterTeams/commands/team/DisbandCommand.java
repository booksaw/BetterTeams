package com.booksaw.betterTeams.commands.team;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

/**
 * This class handles the /team disband command
 * 
 * @author booksaw
 *
 */
public class DisbandCommand extends SubCommand {

	/**
	 * This HashMap is used to track all confirm messages, to ensure that the user
	 * wants to disband the team when they type the command
	 */
	HashMap<UUID, Long> confirmation = new HashMap<>();

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

		UUID found = null;
		// if they have already had the confirm dialogue
		for (Entry<UUID, Long> temp : confirmation.entrySet()) {
			if (temp.getKey().compareTo(p.getUniqueId()) == 0 && temp.getValue() < System.currentTimeMillis() + 10000) {
				found = temp.getKey();
			}
		}

		if (found != null) {
			team.disband();
			confirmation.remove(found);
			return "disband.success";
		}

		confirmation.put(p.getUniqueId(), System.currentTimeMillis());
		return "disband.confirm";

	}

	@Override
	public String getCommand() {
		return "disband";
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
