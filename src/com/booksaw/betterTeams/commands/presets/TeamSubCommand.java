package com.booksaw.betterTeams.commands.presets;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;

/**
 * This class can be extended for any sub commands which require players to be
 * in a team
 * 
 * @author booksaw
 *
 */
public abstract class TeamSubCommand extends SubCommand {

	PlayerRank requiredRank = getDefaultRank();
	protected boolean checkRank = true;

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Player player = (Player) sender;
		Team team = Team.getTeam(player);

		if (team == null) {
			return new CommandResponse("inTeam");
		}
		TeamPlayer teamPlayer = team.getTeamPlayer(player);

		if (checkRank) {
			CommandResponse response = checkRank(teamPlayer);
			if (response != null) {
				return response;
			}
		}

		return onCommand(teamPlayer, label, args, team);
	}

	/**
	 * This method is run if the player is in a team
	 * 
	 * @param player the player who is in a team
	 * @param label  the label for the command
	 * @param args   the arguments for the command
	 * @param team   the team that the player is in
	 * @return the message reference to send to the user
	 */
	public abstract CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team);

	@Override
	public boolean needPlayer() {
		return true;
	}

	/**
	 * @return the rank that the player has to be by default for this command
	 */
	public abstract PlayerRank getDefaultRank();

	public PlayerRank getRequiredRank() {
		return requiredRank;
	}

	public void setRequiredRank(PlayerRank requiredRank) {
		this.requiredRank = requiredRank;
	}

	protected CommandResponse checkRank(TeamPlayer player) {
		return checkRank(player, requiredRank);
	}

	protected CommandResponse checkRank(TeamPlayer player, PlayerRank rank) {

		if (player.getRank() != PlayerRank.OWNER && requiredRank != PlayerRank.DEFAULT) {
			if (requiredRank == PlayerRank.OWNER) {
				return new CommandResponse("needOwner");
			}

			if (player.getRank() != PlayerRank.ADMIN) {
				return new CommandResponse("needAdmin");
			}
		}

		return null;
	}

}
