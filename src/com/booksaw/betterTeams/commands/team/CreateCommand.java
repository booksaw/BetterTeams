package com.booksaw.betterTeams.commands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;

/**
 * This class handles the /team create <team> command
 * 
 * @author booksaw
 *
 */
public class CreateCommand extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		if (Team.getTeam((Player) sender) != null) {
			return "create.leave";
		}

		if (Team.getTeam(args[0]) != null) {
			// team already exists
			return "create.exists";
		}

		Team.createNewTeam(args[0], (Player) sender);

		return "create.success";

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
	public boolean needPlayer() {
		return true;
	}

}
