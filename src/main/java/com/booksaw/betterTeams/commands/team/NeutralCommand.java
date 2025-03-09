package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.Message;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;


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
		if (team.hasRequested(toNeutral)) {
			// removing the ally request
			team.removeAllyRequest(toNeutral);

			// notifying the other team

			Message message = new ReferencedFormatMessage("neutral.reject", team.getDisplayName());
			toNeutral.getMembers().broadcastMessage(message);

			return new CommandResponse(true, "neutral.requestremove");
		}

		// if they are allies
		if (toNeutral.isAlly(team)) {
			toNeutral.becomeNeutral(team, false);
			team.becomeNeutral(toNeutral, true);

			return new CommandResponse(true, "neutral.success");
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
			// Only be able to tab-complete allies
			Team myTeam = getMyTeam(sender);

			Set<UUID> knownTeams = null, ignoreTeam = null;
			if (myTeam != null) {
				knownTeams = myTeam.getAllies().get();
				ignoreTeam = Collections.singleton(myTeam.getID());
			}

			addTeamStringList(options, args[0], ignoreTeam, knownTeams);
		}
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
