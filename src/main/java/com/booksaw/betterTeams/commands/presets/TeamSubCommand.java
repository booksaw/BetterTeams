package com.booksaw.betterTeams.commands.presets;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * This class can be extended for any sub commands which require players to be
 * in a team
 *
 * @author booksaw
 */
public abstract class TeamSubCommand extends SubCommand {

	protected boolean checkRank = true;
	PlayerRank requiredRank = getDefaultRank();

	private final LoadingCache<CommandSender, Team> teamCache = Caffeine.newBuilder()
			.maximumSize(300)
			.expireAfterAccess(Duration.ofMinutes(5))
			.build(this::getTeam);

	private Team getTeam(CommandSender sender) {
		if (sender instanceof Player) {
			return Team.getTeam((Player) sender);
		}
		return null;
	}

	protected @Nullable Team getMyTeam(CommandSender sender) {
		return teamCache.get(sender);
	}

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Player player = (Player) sender;
		Team team = Team.getTeam(player);

		if (team == null) {
			return new CommandResponse("inTeam");
		}
		TeamPlayer teamPlayer = team.getTeamPlayer(player);

		if (teamPlayer == null) {
			Bukkit.getLogger().severe("[BetterTeams] For some reason your storage has desynchronised, set `rebuildLookups` to true in config.yml and restart your server");
			Bukkit.getLogger().severe("[BetterTeams] If this keeps occuring after performing this change, please report it as a bug");
		}

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

	public CommandResponse checkRank(TeamPlayer player) {
		return checkRank(player, requiredRank);
	}

	protected CommandResponse checkRank(TeamPlayer player, PlayerRank rank) {

		if (player.getRank() != PlayerRank.OWNER && rank != PlayerRank.DEFAULT) {
			if (rank == PlayerRank.OWNER) {
				return new CommandResponse("needOwner");
			}

			if (player.getRank() != PlayerRank.ADMIN) {
				return new CommandResponse("needAdmin");
			}
		}

		return null;
	}

}
