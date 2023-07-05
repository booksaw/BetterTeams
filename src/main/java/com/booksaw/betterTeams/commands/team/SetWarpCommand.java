package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

public class SetWarpCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (team.getWarp(args[0]) != null) {
			return new CommandResponse("setwarp.exist");
		}

		String password = "";

		if (args.length >= 2 && Main.plugin.getConfig().getBoolean("allowPassword")) {
			password = args[1];
		}
		// TODO HERE SETTING A MAX WARPS VARIABLE IN THE TEAM CLASS
		int maxWarps = team.getMaxWarps();
		if (team.getWarps().size() >= maxWarps && maxWarps != -1) {
			return new CommandResponse("setwarp.max");
		}

		for (char c : args[0].toCharArray()) {
			if (!Character.isLetterOrDigit(c)) {
				return new CommandResponse("setwarp.char");
			}
		}

		team.addWarp(new Warp(args[0], Objects.requireNonNull(player.getPlayer().getPlayer()).getLocation(), password));

		return new CommandResponse("setwarp.success");
	}

	@Override
	public String getCommand() {
		return "setwarp";
	}

	@Override
	public String getNode() {
		return "setwarp";
	}

	@Override
	public String getHelp() {
		return "Set a warp for the rest of your team";
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
