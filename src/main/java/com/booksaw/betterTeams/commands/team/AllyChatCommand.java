package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.HelpMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AllyChatCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (args.length == 0) {
			// toggle chat
			if (!Main.plugin.getConfig().getBoolean("allowToggleTeamChat")) {
				return new CommandResponse(new HelpMessage(this, label));
			}

			if (player.isInAllyChat()) {
				player.setAllyChat(false);
				return new CommandResponse(true, "allychat.disabled");
			} else {
				player.setTeamChat(false);
				player.setAllyChat(true);
				return new CommandResponse(true, "allychat.enabled");
			}
		}

		StringBuilder message = new StringBuilder();
		for (String arg : args) {
			message.append(arg).append(" ");
		}

		team.sendAllyMessage(player, message.toString());
		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "allychat";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "allychat";
	}

	@Override
	public String getHelp() {
		return "Send a message only to your allies";
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

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.DEFAULT;
	}

}
