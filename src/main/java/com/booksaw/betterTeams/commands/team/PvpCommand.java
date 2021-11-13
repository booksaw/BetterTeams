package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.Message;
import com.booksaw.betterTeams.message.ReferenceMessage;


public class PvpCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (team.isPvp()) {
			team.setPvp(false);
			Message message = new ReferenceMessage("pvp.disabled");
			team.getMembers().broadcastMessage(message);
		} else {
			team.setPvp(true);
			Message message = new ReferenceMessage("pvp.enabled");
			team.getMembers().broadcastMessage(message);
		}

		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "pvp";
	}

	@Override
	public String getNode() {
		return "pvp";
	}

	@Override
	public String getHelp() {
		return "Toggle if pvp is enabled for your team";
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

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.ADMIN;
	}

}
