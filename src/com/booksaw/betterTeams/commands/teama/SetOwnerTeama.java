package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;

public class SetOwnerTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Player p = Bukkit.getPlayer(args[0]);
		if (p == null) {
			return new CommandResponse("noPlayer");
		}

		Team team = Team.getTeam(p);
		if (team == null) {
			return new CommandResponse("admin.inTeam");
		}

		TeamPlayer tp = team.getTeamPlayer(p);
		if (tp.getRank() == PlayerRank.OWNER) {
			return new CommandResponse("admin.setowner.already");
		}

		team.promotePlayer(tp);
		MessageManager.sendMessage(p, "admin.setowner.notify");

		for (TeamPlayer player : team.getMembers().getClone()) {
			if (player.getRank() == PlayerRank.OWNER) {
				if (player == tp) {
					continue;
				}
				team.demotePlayer(player);
				if (player.getPlayer().isOnline()) {
					MessageManager.sendMessage(player.getPlayer().getPlayer(), "admin.setowner.nonotify");
				}
			}
		}

		return new CommandResponse(true, "admin.setowner.success");

	}

	@Override
	public String getCommand() {
		return "setowner";
	}

	@Override
	public String getNode() {
		return "admin.setowner";
	}

	@Override
	public String getHelp() {
		return "Set the player to be their teams owner";
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

}
