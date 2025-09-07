package com.booksaw.betterTeams.commands.presets;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * This class is used for admin commands which reference a specific team
 * remember args[0] will be the team name
 *
 * @author nfgg2
 */
public abstract class TeamSelectSubCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		if (args.length == 0) {
			return new CommandResponse("help");
		}

		Team team = resolveTeam(args[0]);

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


	/**
	 * resolve a team from either a team name or a player name
	 * @param identifier The string which could be a team name or a player name
	 * @return The resolved Team, or null if not found
	 */
	@Nullable
	protected Team resolveTeam(String identifier) {
		if (identifier == null || identifier.isEmpty()) {
			return null;
		}

		Team team = Team.getTeam(identifier);

		if (team == null) {
			Player player = Bukkit.getPlayer(identifier);
			if (player != null) {
				team = Team.getTeam(player);
			}
		}
		return team;
	}
}
