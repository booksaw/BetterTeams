package com.booksaw.betterTeams.commands.team;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.Warp;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;

public class WarpCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {
		if (args.length == 0) {

			String replace = "";
			for (Entry<String, Warp> warp : team.getWarps().entrySet()) {
				replace = replace + warp.getKey() + ", ";
			}

			if (replace.length() == 0) {
				return new CommandResponse("warps.none");
			}

			replace = replace.substring(0, replace.length() - 2);

			return new CommandResponse(new ReferencedFormatMessage("warps.syntax", replace));
		}

		Warp warp = team.getWarp(args[0]);
		if (warp == null) {
			return new CommandResponse("warp.nowarp");
		}

		if (warp.getPassword() != null && !warp.getPassword().equals("")
				&& Main.plugin.getConfig().getBoolean("allowPassword")) {
			if (args.length == 1 || !warp.getPassword().equals(args[1])) {
				return new CommandResponse("warp.invalidPassword");
			}
		}

		// the user is allowed to go to the warp
		warp.execute(player.getPlayer().getPlayer());

		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "warp";
	}

	@Override
	public String getNode() {
		return "warp";
	}

	@Override
	public String getHelp() {
		return "Warp to a location set by your team";
	}

	@Override
	public String getArguments() {
		return "<name>";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
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
