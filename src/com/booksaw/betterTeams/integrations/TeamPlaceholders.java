package com.booksaw.betterTeams.integrations;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.MessageManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

		if (player == null) {
			return "";
		}

		identifier = identifier.toLowerCase();
		// %betterTeams_name%
		if (identifier.equals("name")) {
			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return String.format(MessageManager.getMessage(player, "placeholder.name").trim(), team.getName());
		} else if (identifier.equals("tag")) {
			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return String.format(MessageManager.getMessage(player, "placeholder.tag").trim(), team.getTag());
		} else if (identifier.equals("displayname")) {
			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return String.format(MessageManager.getMessage(player, "placeholder.displayname").trim(),
					team.getColor() + team.getTag());

		} else if (identifier.equals("description")) {

			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}
			if (team.getDescription() == null || team.getDescription().equals("")) {
				return MessageManager.getMessage("placeholder.noDescription");
			}

			return team.getDescription();
		} else if (identifier.equals("open")) {

			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return team.isOpen() + "";
		} else if (identifier.equals("money")) {

			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return String.format(MessageManager.getMessage(player, "placeholder.money").trim(), team.getBalance() + "");
		} else if (identifier.equals("score")) {

			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return team.getScore() + "";
		} else if (identifier.equals("rank")) {
			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			switch (Objects.requireNonNull(team.getTeamPlayer(player)).getRank()) {
			case ADMIN:
				return MessageManager.getMessage(player, "placeholder.admin");
			case DEFAULT:
				return MessageManager.getMessage(player, "placeholder.default");
			case OWNER:
				return MessageManager.getMessage(player, "placeholder.owner");

			}
		} else if (identifier.equals("color")) {
			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return team.getColor() + "";

		} else if (identifier.equals("online")) {
			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return String.valueOf(team.getOnlineMemebers().size());
		} else if (identifier.startsWith("teamscore_")) {
			identifier = identifier.replaceAll("teamscore_", "");
			int place;
			try {
				place = Integer.parseInt(identifier) - 1;
			} catch (NumberFormatException e) {
				return null;
			}
			if (place == -1) {
				return null;
			}

			String[] teams = Team.getTeamManager().sortTeamsByScore();
			if (teams.length <= place) {
				return null;
			} else {
				return Team.getTeam(teams[place]).getName();
			}

		} else if (identifier.startsWith("teamscoreno_")) {
			identifier = identifier.replaceAll("teamscoreno_", "");
			int place;
			try {
				place = Integer.parseInt(identifier) - 1;
			} catch (NumberFormatException e) {
				return null;
			}
			if (place == -1) {
				return null;
			}

			String[] teams = Team.getTeamManager().sortTeamsByScore();
			if (teams.length <= place) {
				return null;
			} else {
				return Team.getTeam(teams[place]).getScore() + "";
			}

		}

		return null;

	}
}
