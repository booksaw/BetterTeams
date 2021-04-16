package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;

public class DescriptionCommand extends TeamSubCommand {

	public DescriptionCommand() {
		checkRank = false;
	}

	@Override
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		if (args.length == 0) {
			if (team.getDescription() != null && !team.getDescription().equals("")) {
				return new CommandResponse(true,
						new ReferencedFormatMessage("description.view", team.getDescription()));
			} else {
				return new CommandResponse("description.noDesc");
			}

		}

		if (teamPlayer.getRank().value < getRequiredRank().value) {
			if (team.getDescription() != null && !team.getDescription().equals("")) {
				MessageManager.sendMessageF(teamPlayer.getPlayer().getPlayer(), "description.view",
						team.getDescription());
			} else {
				MessageManager.sendMessage(teamPlayer.getPlayer().getPlayer(), "description.noDesc");
			}
			return new CommandResponse("description.noPerm");
		}

		String newDescrip = "";
		for (String temp : args) {
			newDescrip = newDescrip + temp + " ";
		}

		team.setDescription(newDescrip);

		return new CommandResponse(true, "description.success");
	}

	@Override
	public String getCommand() {
		return "description";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "description";
	}

	@Override
	public String getHelp() {
		return "View and change your team's description";
	}

	@Override
	public String getArguments() {
		return "[description]";
	}

	@Override
	public int getMaximumArguments() {
		return -1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
