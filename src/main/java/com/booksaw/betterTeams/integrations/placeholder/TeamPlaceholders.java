package com.booksaw.betterTeams.integrations.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.Utils;
import com.booksaw.betterTeams.message.MessageManager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * This class is used to set the placeholder values for placeholder API
 *
 * @author booksaw
 */
public class TeamPlaceholders extends PlaceholderExpansion {
	private final Plugin plugin;

	public TeamPlaceholders(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public @NotNull String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public @NotNull String getIdentifier() {
		return "betterTeams";
	}

	@Override
	public @NotNull String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {

		identifier = identifier.toLowerCase();

		if (identifier.startsWith("position_")) {

			return processRankedTeamDataPlaceholder(identifier, SortType.SCORE);

		} else if (identifier.startsWith("balanceposition_")) {

			return processRankedTeamDataPlaceholder(identifier, SortType.BALANCE);
		} else if (identifier.startsWith("membersposition_")) {

			return processRankedTeamDataPlaceholder(identifier, SortType.MEMBERS);
		} else if (identifier.startsWith("static_")) {

			String[] split = identifier.split("_");
			if (split.length < 3) {
				return null;
			}

			Team team = Team.getTeam(split[1]);
			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}
			return TeamPlaceholderService.getPlaceholder(split[2], team, null);
		} else if (identifier.startsWith("staticplayer_")) {
			String[] split = identifier.split("_");
			if (split.length < 3) {
				return null;
			}
			OfflinePlayer selectedPlayer = Utils.getOfflinePlayer(split[1]);
			if (selectedPlayer == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}
			
			Team team = Team.getTeam(selectedPlayer);
			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}
			
			TeamPlayer tp = team.getTeamPlayer(selectedPlayer);
			if (tp == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}
			
			return TeamPlaceholderService.getPlaceholder(split[2], team, tp);
		} else {
			// base placeholder, simplest case
			// ie %betterteams_name%
			Team team = Team.getTeam(player);
			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			TeamPlayer tp = team.getTeamPlayer(player);

			if (tp == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}
			return TeamPlaceholderService.getPlaceholder(identifier, team, tp);
		}

	}

	private String processRankedTeamDataPlaceholder(String identifier, SortType type) {
		String[] split = identifier.split("_");
		if (split.length < 3) {
			return null;
		}

		int value;
		try {
			value = Integer.parseInt(split[1]);
		} catch (NumberFormatException e) {
			return null;
		}

		if (value <= 0) {
			return null;
		}
		String[] teams;
		switch (type) {
		case BALANCE:
			teams = Team.getTeamManager().sortTeamsByBalance();
			break;
		case MEMBERS:
			teams = Team.getTeamManager().sortTeamsByMembers();
			break;
		default:
			teams = Team.getTeamManager().sortTeamsByScore();
			break;
		}

		Team team;
		try {
			team = Team.getTeam(teams[value - 1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			return MessageManager.getMessage("placeholder.noTeam");
		}

		if (team == null) {
			return MessageManager.getMessage("placeholder.noTeam");
		}

		return TeamPlaceholderService.getPlaceholder(split[2], team, null);
	}

	private enum SortType {
		SCORE, BALANCE, MEMBERS;
	}

}
