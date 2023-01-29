package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.commands.SubCommand;

public class ReloadTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		
		HandlerList.unregisterAll(Main.plugin);
		Main.plugin.reload();

		return new CommandResponse(true, "admin.config.reload");
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
	
	@Override
	public boolean runAsync(String[] args) {
		return false;
	}

}
