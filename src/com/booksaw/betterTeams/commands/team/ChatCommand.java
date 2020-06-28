package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class ChatCommand extends TeamSubCommand {

	@Override
	public String onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (args.length == 0) {
			// toggle chat
			if (!Main.plugin.getConfig().getBoolean("allowToggleTeamChat")) {
				return "help";
			}

			if (player.isInTeamChat()) {
				player.setTeamChat(false);
				return "chat.disabled";
			} else {
				player.setTeamChat(true);
				return "chat.enabled";
			}
		}

		String message = "";
		for (int i = 0; i < args.length; i++) {
			message = message + args[i] + " ";
		}

		team.sendMessage(player, message);
		return null;
	}

	@Override
	public String getCommand() {
		return "chat";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "chat";
	}

	@Override
	public String getHelp() {
		return "Send a message only to your team";
	}

	@Override
	public String getArguments() {
		return "[message]";
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}

	@Override
	public int getMaximumArguments() {
		return -10;
	}

}
