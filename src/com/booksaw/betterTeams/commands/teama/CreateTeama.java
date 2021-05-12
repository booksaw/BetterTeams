package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;

public class CreateTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		if (!Team.isValidTeamName(args[0])) {
			return new CommandResponse("create.banned");
		}

		int max = Main.plugin.getConfig().getInt("maxTeamLength");
		if (max > 55) {
			max = 55;
		}
		if (max != -1 && max < args[0].length()) {
			return new CommandResponse("create.maxLength");
		}

		if (Team.getTeam(args[0]) != null) {
			// team already exists
			return new CommandResponse("create.exists");
		}

		Team.getTeamManager().createNewTeam(args[0], (Player) sender);
		Team team = Team.getTeam(args[0]);
		team.removePlayer((Player) sender);

		return new CommandResponse(true, "admin.create.success");
	}

	@Override
	public String getCommand() {
		return "create";
	}

	@Override
	public String getNode() {
		return "admin.create";
	}

	@Override
	public String getHelp() {
		return "Create a team without an owner";
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
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			options.add("<team>");
		}
	}

	@Override
	public boolean needPlayer() {
		return true;
	}

}
