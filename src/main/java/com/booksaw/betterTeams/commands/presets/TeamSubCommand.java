package com.booksaw.betterTeams.commands.presets;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.util.Cache;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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
	@Setter
	@Getter
	PlayerRank requiredRank = getDefaultRank();

	private final Cache<CommandSender, Team> teamCache = new Cache.Builder<CommandSender, Team>()
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
		if (!(sender instanceof Player)) return null;

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

	public CommandResponse checkRank(TeamPlayer player) {
		return checkRank(player, requiredRank);
	}

	protected CommandResponse checkRank(@NotNull TeamPlayer player, PlayerRank rank) {

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

	@Getter
	protected static class TeamPlayerResult {
		private final @Nullable CommandResponse cr;
		private final @Nullable TeamPlayer player;

		TeamPlayerResult(@NotNull String cr) {
			this.cr = new CommandResponse(cr);
			this.player = null;
		}

		TeamPlayerResult(@Nullable TeamPlayer player) {
			this.cr = null;
			this.player = player;
		}

		public boolean isCR() {
			return cr != null;
		}
	}

	protected TeamPlayerResult getTeamPlayer(final Team team, final String name) {
		OfflinePlayer player = Utils.getOfflinePlayer(name);

		if (player == null) {
			return new TeamPlayerResult("noPlayer");
		}

		Team otherTeam = Team.getTeam(player);
		if (team != otherTeam) {
			return new TeamPlayerResult("needSameTeam");
		}

		return new TeamPlayerResult(team.getTeamPlayer(player));
	}
}
