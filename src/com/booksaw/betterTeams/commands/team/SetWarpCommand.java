package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.Warp;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class SetWarpCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (team.getWarp(args[0]) != null) {
			return new CommandResponse("setwarp.exist");
		}

		if (player.getRank() == PlayerRank.DEFAULT) {
			return new CommandResponse("needAdmin");
		}

		String password = "";

		if (args.length >= 2 && Main.plugin.getConfig().getBoolean("allowPassword")) {
			password = args[1];
		}

		if (team.getWarps().size() >= Main.plugin.getConfig().getInt("maxWarps")) {
			return new CommandResponse("setwarp.max");
		}

		team.addWarp(new Warp(args[0], player.getPlayer().getPlayer().getLocation(), password));

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
		return "<name>";
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

}
