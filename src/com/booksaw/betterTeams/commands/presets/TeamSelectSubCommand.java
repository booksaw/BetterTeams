package com.booksaw.betterTeams.commands.presets;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;

/**
 * This class is used for admin commands which reference a specific team
 * remember args[0] will be the team name
 * 
 * @author nfgg2
 *
 */
public abstract class TeamSelectSubCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		if (args.length == 0) {
			return new CommandResponse("help");
		}

		Team team = Team.getTeam(args[0]);

		if (team == null) {
			return new CommandResponse("admin.noTeam");
		}
		return onCommand(sender, label, args, team);
	}

	/**
	 * This method is run if the player is in a team
	 * 
	 * @param sender the player who ran the commnad
	 * @param label  the label for the command
	 * @param args   the arguments for the command
	 * @param team   the team that the player is in
	 * @return the message reference to send to the user
	 */
	public abstract CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team);

	@Override
	public boolean needPlayer() {
		return true;
	}
}
