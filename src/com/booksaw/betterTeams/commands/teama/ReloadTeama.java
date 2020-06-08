package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.events.ChatManagement;

public class ReloadTeama extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		Main.plugin.reloadConfig();
		Main.plugin.loadCustomConfigs();
		Team.loadTeams();
		ChatManagement.enable();

		return "admin.config.reload";
	}

	@Override
	public String getCommand() {
		return "reload";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "admin.reload";
	}

	@Override
	public String getHelp() {
		return "Reload the BetterTeams configs";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

	}

}
