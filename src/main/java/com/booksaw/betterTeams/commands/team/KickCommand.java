package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class KickCommand extends TeamSubCommand {

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

		TeamPlayer kickedPlayer = teamPlayerResult.getPlayer();

		// ensuring the player they are banning has less perms than them
		if (teamPlayer.getRank().value <= Objects.requireNonNull(kickedPlayer).getRank().value) {
			return new CommandResponse("kick.noPerm");
		}

		team.removePlayer(kickedPlayer);

		Player player = kickedPlayer.getPlayer().getPlayer();
		if (player != null) {
			MessageManager.sendMessage(player, "kick.notify", team.getName());

			if (Main.plugin.getConfig().getBoolean("titleRemoval")) {
				MessageManager.sendTitle(player, "kick.title");
			}

		}

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

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.ADMIN;
	}

}
