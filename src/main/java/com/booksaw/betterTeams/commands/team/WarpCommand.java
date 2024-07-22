package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {
		if (args.length == 0) {

			StringBuilder replace = new StringBuilder();
			for (Warp warp : team.getWarps().get()) {
				replace.append(warp.getName()).append(", ");
			}

			if (replace.length() == 0) {
				return new CommandResponse("warps.none");
			}

			replace = new StringBuilder(replace.substring(0, replace.length() - 2));

			return new CommandResponse(new ReferencedFormatMessage("warps.syntax", replace.toString()));
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
		try {
			warp.execute(player.getPlayer().getPlayer());
		} catch (Exception e) {
			return new CommandResponse("warp.world");
		}

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
		return PlayerRank.DEFAULT;
	}

}
