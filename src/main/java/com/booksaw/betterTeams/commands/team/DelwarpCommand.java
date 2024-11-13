package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DelwarpCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {
		Warp warp = team.getWarp(args[0]);
		if (warp == null) {
			return new CommandResponse("warp.nowarp");
		}

		if (player.getRank() == PlayerRank.ADMIN) {
			if (warp.getPassword() != null && !warp.getPassword().isEmpty()
					&& Main.plugin.getConfig().getBoolean("allowPassword")) {
				if (args.length == 1 || !warp.getPassword().equals(args[1])) {
					return new CommandResponse("warp.invaildPassword");
				}
			}
		}
		team.delWarp(args[0]);

		return new CommandResponse(true, "delwarp.success");
	}

	@Override
	public String getCommand() {
		return "delwarp";
	}

	@Override
	public String getNode() {
		return "delwarp";
	}

	@Override
	public String getHelp() {
		return "Delete that warp from your team";
	}

	@Override
	public String getArguments() {
		return "<name> [password]";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			if (sender instanceof Player) {
				Team team = Team.getTeam((Player) sender);

				if (team == null) {
					options.add("<name>");
					return;
				}

				for (Warp temp : team.getWarps().get()) {
					if (temp.getName().startsWith(args[0])) {
						options.add(temp.getName());
					}
				}
				return;
			}

			options.add("<name>");
		}
		if (args.length == 2 && Main.plugin.getConfig().getBoolean("allowPassword")) {
			options.add("[password]");
		}
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.ADMIN;
	}

}
