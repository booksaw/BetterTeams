package com.booksaw.betterTeams.commands.teama;

import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;

public class PromoteTeama extends SubCommand {

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
		if (Objects.requireNonNull(tp).getRank() == PlayerRank.OWNER) {
			return new CommandResponse("admin.promote.max");
		} else if (tp.getRank() == PlayerRank.ADMIN && Main.plugin.getConfig().getBoolean("singleOwner")) {
			return new CommandResponse("admin.promote.owner");
		}

		team.promotePlayer(tp);
		MessageManager.sendMessage(p, "admin.promote.notify");
		return new CommandResponse(true, "admin.promote.success");
	}

	@Override
	public String getCommand() {
		return "promote";
	}

	@Override
	public String getNode() {
		return "admin.promote";
	}

	@Override
	public String getHelp() {
		return "Promote that player within their team";
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
