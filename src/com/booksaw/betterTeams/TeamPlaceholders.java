package com.booksaw.betterTeams;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.booksaw.betterTeams.message.MessageManager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * This class is used to set the placeholder values for placeholder API
 * 
 * @author booksaw
 *
 */
public class TeamPlaceholders extends PlaceholderExpansion {
	private Plugin plugin;

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
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return "betterTeams";
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {

		if (player == null) {
			return "";
		}

		// %betterTeams_name%
		if (identifier.equals("name")) {
			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return String.format(MessageManager.getMessage("placeholder.name"), team.getName());

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

			return team.getBalance();
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

			switch (team.getTeamPlayer(player).getRank()) {
			case ADMIN:
				return MessageManager.getMessage("placeholder.admin");
			case DEFAULT:
				return MessageManager.getMessage("placeholder.default");
			case OWNER:
				return MessageManager.getMessage("placeholder.owner");

			}
		} else if (identifier.equals("color")) {
			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return team.getColor() + "";

		} else if (identifier.startsWith("teamscore_")) {
			identifier = identifier.replaceAll("teamscore_", "");
			int place = -1;
			try {
				place = Integer.parseInt(identifier) - 1;
			} catch (NumberFormatException e) {
				return null;
			}
			if (place == -1) {
				return null;
			}

			Team[] teams = Team.sortTeamsByScore();
			if (teams.length <= place) {
				return null;
			} else {
				return teams[place].getName();
			}

		} else if (identifier.startsWith("teamscoreno_")) {
			identifier = identifier.replaceAll("teamscoreno_", "");
			int place = -1;
			try {
				place = Integer.parseInt(identifier) - 1;
			} catch (NumberFormatException e) {
				return null;
			}
			if (place == -1) {
				return null;
			}

			Team[] teams = Team.sortTeamsByScore();
			if (teams.length <= place) {
				return null;
			} else {
				return teams[place].getScore() + "";
			}

		}

		return null;

	}
}
