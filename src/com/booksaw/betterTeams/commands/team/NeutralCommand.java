package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.Message;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;


public class NeutralCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		// getting the referenced team
		Team toNeutral = Team.getTeam(args[0]);
		if (toNeutral == null) {
			return new CommandResponse("noTeam");
		} else if (toNeutral == team) {
			return new CommandResponse("neutral.self");
		}

		// if there is an ally request
		if (team.hasRequested(toNeutral.getID())) {
			// removing the ally request
			team.removeAllyRequest(toNeutral.getID());

			// notifying the other team

			Message message = new ReferencedFormatMessage("neutral.reject", team.getDisplayName());
			toNeutral.getMembers().broadcastMessage(message);

			return new CommandResponse(true, "neutral.requestremove");
		}

		// if they are allies
		if (toNeutral.isAlly(team.getID())) {
			toNeutral.removeAlly(team.getID());
			team.removeAlly(toNeutral.getID());

			// notifying both teams
			Message message = new ReferencedFormatMessage("neutral.remove", team.getDisplayName());
			toNeutral.getMembers().broadcastMessage(message);

			// notifying the other team
			message = new ReferencedFormatMessage("neutral.remove", toNeutral.getDisplayName());
			team.getMembers().broadcastMessage(message);
			return new CommandResponse(true);
		}

		return new CommandResponse("neutral.notAlly");
	}

	@Override
	public String getCommand() {
		return "neutral";
	}

	@Override
	public String getNode() {
		return "neutral";
	}

	@Override
	public String getHelp() {
		return "Remove allies and reject ally requests";
	}

	@Override
	public String getArguments() {
		return "<team>";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		}
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
