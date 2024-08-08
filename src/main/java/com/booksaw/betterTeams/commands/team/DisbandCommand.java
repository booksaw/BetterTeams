package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * This class handles the /team disband command
 *
 * @author booksaw
 */
public class DisbandCommand extends TeamSubCommand {

	/**
	 * This HashMap is used to track all confirm messages, to ensure that the user
	 * wants to disband the team when they type the command
	 */
	final HashMap<UUID, Long> confirmation = new HashMap<>();

	@Override
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {
		final UUID playerUuid = teamPlayer.getPlayer().getUniqueId();

		if (
			// can use secret command /team disband confirm to validate disband success
				(args.length > 1 && args[0].equals("confirm"))
				// double usage of disband command within 10s
				|| (confirmation.containsKey(playerUuid) && confirmation.get(playerUuid) < System.currentTimeMillis() + 10000)
		) {
			team.disband();
			confirmation.remove(playerUuid);
			return new CommandResponse(true, "disband.success");
		}

		confirmation.put(playerUuid, System.currentTimeMillis());
		return new CommandResponse("disband.confirm");
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
	public String getNode() {
		return "disband";
	}

	@Override
	public String getHelp() {
		return "Disband your current team";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
