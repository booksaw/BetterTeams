package com.booksaw.betterTeams.commands.team;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;

public class ListCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		int page;
		if (args.length == 0) {
			page = 0;
		} else {
			try {
				page = Integer.parseInt(args[0]) - 1;
			} catch (NumberFormatException e) {
				return new CommandResponse("help");
			}
		}

		Team[] teams = new Team[Team.getTeamList().size()];
		int y = 0;

		for (Entry<UUID, Team> temp : Team.getTeamList().entrySet()) {
			teams[y] = temp.getValue();
			y++;
		}

		// bubble sort
		for (int i = 0; i < teams.length - 1; i++) {
			boolean changes = false;

			for (int j = 0; j < teams.length - i - 1; j++) {
				if (teams[j].getMembers().size() < teams[j + 1].getMembers().size()) {
					Team temp = teams[j];
					teams[j] = teams[j + 1];
					teams[j + 1] = temp;
					changes = true;
				}
			}

			if (!changes) {
				break;
			}
		}

		// displaying the page
		if (page * 10 > teams.length) {
			return new CommandResponse("list.noPage");
		}

		MessageManager.sendMessageF(sender, "list.header", (page + 1) + "");
		for (int i = page * 10; i < (page + 1) * 10 && i < teams.length; i++) {
			MessageManager.sendMessageF(sender, "list.body", (i + 1) + "", teams[i].getDisplayName());
		}

		MessageManager.sendMessage(sender, "list.footer");

		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "list";
	}

	@Override
	public String getNode() {
		return "list";
	}

	@Override
	public String getHelp() {
		return "Get a list of all teams on the server";
	}

	@Override
	public String getArguments() {
		return "[page]";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

	}

}
