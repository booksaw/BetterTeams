package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class DemoteTeama extends SubCommand {

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
		if (Objects.requireNonNull(tp).getRank() == PlayerRank.DEFAULT) {
			return new CommandResponse("admin.demote.min");
		}

		if (tp.getRank() == PlayerRank.OWNER&& team.isMaxAdmins()) {
			return new CommandResponse("admin.demote.maxAdmins");
		} 
		
		team.demotePlayer(tp);
		MessageManager.sendMessage(p, "admin.demote.notify");
		return new CommandResponse(true, "admin.demote.success");
	}

	@Override
	public String getCommand() {
		return "demote";
	}

	@Override
	public String getNode() {
		return "admin.demote";
	}

	@Override
	public String getHelp() {
		return "Demote a player within their team";
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
