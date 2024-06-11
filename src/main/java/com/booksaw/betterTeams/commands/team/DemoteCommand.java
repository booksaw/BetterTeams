package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

/**
 * This class handles the command /team demote [player]
 *
 * @author booksaw
 */
public class DemoteCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		/*
		 * method is depreciated as it does not guarantee the expected player, in most
		 * use cases this will work and it will be down to the user if it does not due
		 * to name changes This method is appropriate to use in this use case (so users
		 * can view offline users teams by name not just by team name)
		 */
		OfflinePlayer player = Utils.getOfflinePlayer(args[0]);

		if (player == null) {
			return new CommandResponse("noPlayer");
		}

		Team otherTeam = Team.getTeam(player);
		if (team != otherTeam) {
			return new CommandResponse("needSameTeam");
		}

		TeamPlayer demotePlayer = team.getTeamPlayer(player);

		if (Objects.requireNonNull(demotePlayer).getRank() == PlayerRank.DEFAULT) {
			return new CommandResponse("demote.min");
		}

		// checking there is another owner
		if (demotePlayer.getRank() == PlayerRank.OWNER && team.getRank(PlayerRank.OWNER).size() == 1) {
			return new CommandResponse("demote.lastOwner");
		}
		// all is good, continue to demotion
		if (demotePlayer.getRank().value >= teamPlayer.getRank().value
				&& !demotePlayer.getPlayer().getUniqueId().equals(teamPlayer.getPlayer().getUniqueId())) {
			// the other person is also an owner, players cannot demote other owners
			return new CommandResponse("demote.noPerm");
		}

		if (demotePlayer.getRank() == PlayerRank.OWNER && team.isMaxAdmins()) {
			return new CommandResponse("demote.maxAdmins");
		}

		team.demotePlayer(demotePlayer);

		OfflinePlayer offlineDemotePlayer = demotePlayer.getPlayer();
		if (offlineDemotePlayer.isOnline()) {
			MessageManager.sendMessage(offlineDemotePlayer.getPlayer(), "demote.notify");
		}

		return new CommandResponse(true, "demote.success");

	}

	@Override
	public String getCommand() {
		return "demote";
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

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}
}
