package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class BalCommand extends TeamSubCommand {

	@Override
	public String onCommand(TeamPlayer player, String label, String[] args, Team team) {
		MessageManager.sendMessageF(player.getPlayer().getPlayer(), "info.money", team.getBalance());
		return null;
	}

	@Override
	public String getCommand() {
		return "bal";
	}

	@Override
	public String getNode() {
		return "balance";
	}

	@Override
	public String getHelp() {
		return "View your teams balance";
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

}
