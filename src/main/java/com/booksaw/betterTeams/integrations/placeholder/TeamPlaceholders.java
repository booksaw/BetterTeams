package com.booksaw.betterTeams.integrations.placeholder;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.Utils;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.util.Cache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * This class is used to set the placeholder values for placeholder API
 *
 * @author booksaw
 */
public class TeamPlaceholders extends PlaceholderExpansion {
	private final Plugin plugin;

	private final Cache<String, String> placeholderCache;

	public TeamPlaceholders(Plugin plugin) {
		this.plugin = plugin;
		placeholderCache = new Cache.Builder<String, String>()
				.maximumSize(300)
				.expireAfterWrite(Duration.ofSeconds(plugin.getConfig().getInt("invalidateCacheSeconds")))
				.build(this::getStaticPlaceholder);
	}

	public void invalidateCache() {
		placeholderCache.invalidateAll();
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
		String originalIdentifier = identifier;
		identifier = identifier.toLowerCase();
		String[] split = identifier.split("_");

		// Case 1: simple placeholders like %betterteams_name%
		if (split.length == 1) {
			if (player == null) {
				return null;
			}

			if ("inteam".equalsIgnoreCase(split[0])) {
				return Team.getTeamManager().isInTeam(player)
						? MessageManager.getMessage("placeholder.inteam")
						: MessageManager.getMessage("placeholder.notinteam");
			}

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

		// Case 2: dynamic placeholders that require data (like %betterteams_meta_key%)
		TeamPlaceholderOptionsEnum option = TeamPlaceholderOptionsEnum.getEnumValue(split[0]);
		if (option != null && option.requiresData()) {
			if (player == null) {
				return null;
			}
			Team team = Team.getTeam(player);
			if (team == null) {
				return MessageManager.getMessage("placeholder.noTeam");
			}
			TeamPlayer tp = team.getTeamPlayer(player);
			String data = originalIdentifier.substring(originalIdentifier.indexOf('_') + 1);

			return option.applyPlaceholderProvider(team, tp, data);
		}

		// Case 3: static or leaderboard placeholders (cacheable)
		return placeholderCache.get(identifier);
	}

	private String getStaticPlaceholder(String identifier) {
		String[] split = identifier.split("_");

		// more complex request though not individual player related so can be cached
		switch (split[0]) {
			case "position":
				return processRankedTeamDataPlaceholder(identifier, SortType.SCORE);
			case "balanceposition":
				return processRankedTeamDataPlaceholder(identifier, SortType.BALANCE);
			case "membersposition":
				return processRankedTeamDataPlaceholder(identifier, SortType.MEMBERS);
			case "static":
				return processStaticTeamPlaceholder(split);
			case "staticplayer_":
				return processStaticTeamPlayerPlaceholder(split);
			default:
				return null;
		}
	}

	private String processRankedTeamDataPlaceholder(String identifier, SortType type) {
		String[] split = identifier.split("_");
		if (split.length < 3) {
			return null;
		}

		int value;
		try {
			value = Integer.parseInt(split[2]);
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

		return TeamPlaceholderService.getPlaceholder(split[1], team, null);
	}

	private String processStaticTeamPlaceholder(String[] split) {

		if (split.length < 3) {
			return null;
		}

		Team team = Team.getTeam(split[2]);
		if (team == null) {
			return MessageManager.getMessage("placeholder.noTeam");
		}
		return TeamPlaceholderService.getPlaceholder(split[1], team, null);
	}

	private String processStaticTeamPlayerPlaceholder(String[] split) {
		if (split.length < 3) {
			return null;
		}
		OfflinePlayer selectedPlayer = Utils.getOfflinePlayer(split[2]);
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

		return TeamPlaceholderService.getPlaceholder(split[1], team, tp);
	}

	private enum SortType {
		SCORE, BALANCE, MEMBERS
	}

	private String getMetaValue(Team team, String key) {
		return team.getMeta().get().get(key).orElse(MessageManager.getMessage("placeholder.noTeam"));
	}
}
