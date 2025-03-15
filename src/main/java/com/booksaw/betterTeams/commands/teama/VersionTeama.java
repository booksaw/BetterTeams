package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VersionTeama extends SubCommand {

	private final String command;

	public VersionTeama(String command) {
		this.command = command;
	}

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		MessageManager.sendMessage(sender, "admin.versionstorage", Team.getTeamManager().getClass().getName());
		MessageManager.sendMessage(sender, "admin.versionversion", Main.plugin.getServer().getVersion());
		MessageManager.sendMessage(sender, "admin.versionlanguage", MessageManager.getLanguage());
		MessageManager.sendMessage(sender, "admin.versiononline", Boolean.toString(Bukkit.getOnlineMode()));
		MessageManager.sendMessage(sender, "admin.versionplayers", Integer.toString(Bukkit.getOnlinePlayers().size()));
		return new CommandResponse(true,
				new ReferencedFormatMessage("admin.version", Main.plugin.getDescription().getVersion()));


	}

	@Override
	public String getCommand() {
		return command;
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
