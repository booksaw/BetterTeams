package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;

public class VersionTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		return new CommandResponse(true,
				new ReferencedFormatMessage("admin.version", Main.plugin.getDescription().getVersion() + ""));
	}

	@Override
	public String getCommand() {
		return "version";
	}

	@Override
	public String getNode() {
		return "admin.version";
	}

	@Override
	public String getHelp() {
		return "Check the plugin version";
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
