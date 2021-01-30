package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;

public class JoinTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Player p = Bukkit.getPlayer(args[1]);
		if (p == null) {
			return new CommandResponse("noPlayer");
		}

		Team test = Team.getTeam(p);
		if (test != null) {
			return new CommandResponse("admin.notInTeam");
		}

		Team team = Team.getTeam(args[0]);
		if (team == null) {
			return new CommandResponse("notTeam");
		}

		if (team.isBanned(p)) {
			return new CommandResponse("admin.join.banned");
		}

		int limit = Main.plugin.getConfig().getInt("levels.l" + team.getLevel() + ".teamLimit");

		if (limit > 0 && limit <= team.getMembers().size()) {
			return new CommandResponse("admin.join.full");
		}

		team.join(p);
		MessageManager.sendMessageF(p, "admin.join.notify", team.getDisplayName());

		return new CommandResponse(true, "admin.join.success");
	}

	@Override
	public String getCommand() {
		return "join";
	}

	@Override
	public String getNode() {
		return "admin.join";
	}

	@Override
	public String getHelp() {
		return "Force a player to join a team";
	}

	@Override
	public String getArguments() {
		return "<team> <player>";
	}

	@Override
	public int getMinimumArguments() {
		return 2;
	}

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		} else if (args.length == 2) {
			addPlayerStringList(options, args[1]);
		}
	}

}
