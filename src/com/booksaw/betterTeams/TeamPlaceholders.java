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

			return team.getName();

		}

		// %betterTeams_rank%
		if (identifier.equals("rank")) {

			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return team.getTeamPlayer(player).getRank().toString();
		}

		if (identifier.equals("description")) {

			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}
			if (team.getDescription() == null || team.getDescription().equals("")) {
				return MessageManager.getMessage("placeholder.noDescription");
			}

			return team.getDescription();
		}

		if (identifier.equals("open")) {

			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return team.isOpen() + "";
		}

		if (identifier.equals("money")) {

			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return team.getBalance();
		}

		if (identifier.equals("score")) {

			Team team = Team.getTeam(player);

			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}

			return team.getScore() + "";
		}

		if (identifier.equals("rank")) {
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
		}

		return null;

	}
}
