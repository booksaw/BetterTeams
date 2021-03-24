package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;

public class LeaveTeama extends SubCommand {

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

		if (team.removePlayer(p)) {
			MessageManager.sendMessage(p, "admin.leave.notify");
			return new CommandResponse(true, "admin.leave.success");
		}
		return new CommandResponse("admin.cancel");
	}

	@Override
	public String getCommand() {
		return "leave";
	}

	@Override
	public String getNode() {
		return "admin.leave";
	}

	@Override
	public String getHelp() {
		return "Force a player to leave a team";
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
