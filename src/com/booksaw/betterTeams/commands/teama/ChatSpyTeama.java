package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.commands.SubCommand;

public class ChatSpyTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		if (Main.plugin.chatManagement.spy.contains(sender)) {
			Main.plugin.chatManagement.spy.remove(sender);
			return new CommandResponse(true, "spy.stop");
		}

		Main.plugin.chatManagement.spy.add(sender);

		return new CommandResponse("true, spy.start");
	}

	@Override
	public String getCommand() {
		return "chatspy";
	}

	@Override
	public String getNode() {
		return "admin.chatspy";
	}

	@Override
	public String getHelp() {
		return "Spy on messages sent to team chats";
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
