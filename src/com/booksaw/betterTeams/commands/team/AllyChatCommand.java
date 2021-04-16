package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.HelpMessage;

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

		String message = "";
		for (int i = 0; i < args.length; i++) {
			message = message + args[i] + " ";
		}

		team.sendAllyMessage(player, message);
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
