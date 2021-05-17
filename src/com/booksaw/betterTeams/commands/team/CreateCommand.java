package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.NoTeamSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class handles the /team create [team] command
 *
 * @author booksaw
 */
public class CreateCommand extends NoTeamSubCommand {

	@Override
	public CommandResponse onCommand(Player sender, String label, String[] args) {

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

		Team.getTeamManager().createNewTeam(args[0], sender);

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
