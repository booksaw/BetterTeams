package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.command.CommandSender;

import java.util.List;

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

		MessageManager.sendMessage(sender, "loading");

		Main.plugin.getFoliaLib().getScheduler().runAsync(task -> {
			String[] teams = Team.getTeamManager().sortTeamsByMembers();

			// displaying the page
			if (page * 10 > teams.length) {
				MessageManager.sendMessage(sender, "list.noPage");
				return;
			}

			MessageManager.sendMessage(sender, "list.header", page + 1);
			for (int i = page * 10; i < (page + 1) * 10 && i < teams.length; i++) {
				MessageManager.sendMessage(sender, "list.body", i + 1, teams[i]);
			}

			MessageManager.sendMessage(sender, "list.footer");
		});
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
