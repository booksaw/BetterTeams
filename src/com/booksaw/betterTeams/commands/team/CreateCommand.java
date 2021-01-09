package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.NoTeamSubCommand;

/**
 * This class handles the /team create [team] command
 * 
 * @author booksaw
 *
 */
public class CreateCommand extends NoTeamSubCommand {

	@Override
	public CommandResponse onCommand(Player sender, String label, String[] args) {

		for (String temp : Main.plugin.getConfig().getStringList("blacklist")) {
			if (temp.toLowerCase().equals(args[0].toLowerCase())) {
				return new CommandResponse("create.banned");
			}
		}
		
		for (char temp : Main.plugin.getConfig().getString("bannedChars").toCharArray()) {
			if (args[0].contains(Character.toString(temp))) {
				return new CommandResponse("create.banned");
			}
		}

		// stop players inputting color codes
		if (args[0].contains("&")) {
			return new CommandResponse("create.banned");
		}
		
		int max = Main.plugin.getConfig().getInt("maxTeamLength");
		if (max != -1 && max < args[0].length()) {
			return new CommandResponse("create.maxLength");
		}

		if (Team.getTeam(args[0]) != null) {
			// team already exists
			return new CommandResponse("create.exists");
		}

		Team.createNewTeam(args[0], (Player) sender);

		return new CommandResponse(true, "create.success");

	}

	@Override
	public String getCommand() {
		return "create";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public String getNode() {
		return "create";
	}

	@Override
	public String getHelp() {
		return "Create a team with the specified name";
	}

	@Override
	public String getArguments() {
		return "<name";
	}

	@Override
	public int getMaximumArguments() {
		return -1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

	}

}
