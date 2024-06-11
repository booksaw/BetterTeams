package com.booksaw.betterTeams.commands.teama.money;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.MoneySubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetMoney extends MoneySubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, Team team, double change) {

		team.setMoney(change);

		return new CommandResponse("admin.bal.success");
	}

	@Override
	public String getCommand() {
		return "set";
	}

	@Override
	public String getNode() {
		return "admin.money.set";
	}

	@Override
	public String getHelp() {
		return "Set the specified teams balance";
	}

	@Override
	public String getArguments() {
		return "<player/team> <balance>";
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
			addPlayerStringList(options, args[0]);
		} else if (args.length == 2) {
			options.add("<balance>");
		}
	}

}
