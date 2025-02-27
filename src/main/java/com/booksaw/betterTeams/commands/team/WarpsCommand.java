package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class WarpsCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		StringBuilder replace = new StringBuilder();
		for (Warp warp : team.getWarps().get()) {
			replace.append(warp.getName()).append(", ");
		}

		if (replace.length() == 0) { // JDK 8 doesn't have StringBuilder::isEmpty yet
			return new CommandResponse("warps.none");
		}

		replace = new StringBuilder(replace.substring(0, replace.length() - 2));

		return new CommandResponse(new ReferencedFormatMessage("warps.syntax", replace.toString()));
	}

	@Override
	public String getCommand() {
		return "warps";
	}

	@Override
	public String getNode() {
		return "warps";
	}

	@Override
	public String getHelp() {
		return "View a list of your teams warps";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.DEFAULT;
	}

}
