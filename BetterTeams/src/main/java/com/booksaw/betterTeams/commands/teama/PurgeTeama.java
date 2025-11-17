package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PurgeTeama extends SubCommand {

	long confirm = 0;

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		if (System.currentTimeMillis() - 10000 > confirm) {
			confirm = System.currentTimeMillis();
			return new CommandResponse("admin.purge.confirm");
		}

		boolean money = true;
		boolean score = true;

		if (args.length >= 1) {
			if (args[0].equals("money")) {
				score = false;
			} else if (args[0].equals("score")) {
				money = false;
			}
		}

		if (Team.getTeamManager().purgeTeams(money, score))
			return new CommandResponse(true, "admin.purge.success");

		return new CommandResponse(false);
	}

	@Override
	public String getCommand() {
		return "purge";
	}

	@Override
	public String getNode() {
		return "admin.purge";
	}

	@Override
	public String getHelp() {
		return "Reset the score and money for all teams, if you specify an value (i.e. money) only that will be reset";
	}

	@Override
	public String getArguments() {
		return "[score/money]";
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
		if (args.length == 1) {
			options.add("score");
			options.add("money");
		}
	}

}
