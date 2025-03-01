package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

public class SetOwnerCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {
		/*
		 * method is depreciated as it does not guarantee the expected player, in most
		 * use cases this will work and it will be down to the user if it does not due
		 * to name changes This method is appropriate to use in this use case (so users
		 * can view offline users teams by name not just by team name)
		 */
		TeamPlayerResult teamPlayerResult = getTeamPlayer(team, args[0]);
		if (teamPlayerResult.isCR()) {
			return teamPlayerResult.getCr();
		}

		TeamPlayer promotePlayer = teamPlayerResult.getPlayer();

		if (Objects.requireNonNull(promotePlayer).getRank() == PlayerRank.OWNER) {
			return new CommandResponse("setowner.max");
		}

		team.promotePlayer(promotePlayer);
		team.demotePlayer(teamPlayer);
		if (promotePlayer.getPlayer().isOnline()) {
			MessageManager.sendMessage((CommandSender) promotePlayer.getPlayer(), "setowner.notify");
		}

		return new CommandResponse(true, "setowner.success");
	}

	@Override
	public String getCommand() {
		return "setowner";
	}

	@Override
	public String getNode() {
		return "setowner";
	}

	@Override
	public String getHelp() {
		return "Set the teams new owner";
	}

	@Override
	public String getArguments() {
		return "<player>";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addPlayerStringList(options, args[0]);
		}
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
