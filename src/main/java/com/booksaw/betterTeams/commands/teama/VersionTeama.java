package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VersionTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		MessageManager.sendMessageF(sender, "admin.versionstorage", Team.getTeamManager().getClass().getName());
		MessageManager.sendMessageF(sender, "admin.versionversion", Main.plugin.getServer().getVersion());
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
