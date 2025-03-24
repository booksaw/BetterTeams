package com.booksaw.betterTeams.events;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.BelowNameChangeEvent;
import com.booksaw.betterTeams.customEvents.BelowNameChangeEvent.ChangeType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

public class MCTeamManagement implements Listener {

	final Scoreboard board;
	@Getter
	private final BelowNameType type;

	/**
	 * Used to track a list of all listeners
	 *
	 * @param type The type prefixing that should be done
	 */
	public MCTeamManagement(BelowNameType type) {
		this.type = type;

		Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
		board = Bukkit.getScoreboardManager().getMainScoreboard();

	}

	public void displayBelowNameForAll() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			displayBelowName(p);
		}
	}

	public void displayBelowName(Player player) {
		player.setScoreboard(board);

		Team team = Team.getTeam(player);
		if (team == null) {
			return;
		}

		// checking the player has the correct permission node
		if (!player.hasPermission("betterTeams.teamName")) {
			// player does not have permission to have their team name displayed.
			return;
		}

		BelowNameChangeEvent event = new BelowNameChangeEvent(player, ChangeType.ADD);
		Bukkit.getPluginManager().callEvent(event);

		try {
			team.getScoreboardTeam(board).addEntry(player.getName());
		} catch (IllegalStateException e) {
			Main.plugin.getLogger().severe("Could not register the team name in the tab menu due to a conflict, see https://github.com/booksaw/BetterTeams/wiki/Managing-the-TAB-Menu error:" + e.getMessage());
		}

	}

	public void removeAll() {
		removeAll(true);
	}

	/**
	 * Used when the plugin is disabled
	 */
	public void removeAll(boolean callEvent) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			remove(p, callEvent);
		}

		// only loaded teams will have a team manager
		for (Entry<UUID, Team> t : Team.getTeamManager().getLoadedTeamListClone().entrySet()) {
			org.bukkit.scoreboard.Team team = t.getValue().getScoreboardTeamOrNull();

			if (team != null) {
				team.unregister();
			}

		}
	}

	public void remove(Player player) {
		remove(player, true);
	}

	/**
	 * Used to remove the prefix / suffix from the specified player
	 *
	 * @param player    the player to remove the prefix/suffix from
	 * @param callEvent if BelowNameChangeEvent should be called
	 */
	public void remove(Player player, boolean callEvent) {

		if (player == null) {
			return;
		}

		Team team = Team.getTeam(player);
		if (team == null) {
			return;
		}

		if (!team.getScoreboardTeam(board).hasEntry(player.getName())) {
			return;
		}

		try {
			team.getScoreboardTeam(board).removeEntry(player.getName());
		} catch (Exception e) {
			Main.plugin.getLogger().warning(
					"Another plugin is conflicting with the functionality of the BetterTeams. See the wiki page: https://github.com/booksaw/BetterTeams/wiki/Managing-the-TAB-Menu for more information");
			return;
		}

		if (callEvent) {
			BelowNameChangeEvent event = new BelowNameChangeEvent(player, ChangeType.REMOVE);
			Bukkit.getPluginManager().callEvent(event);
		}
	}

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> displayBelowName(e.getPlayer()));
	}

	public void setupTeam(org.bukkit.scoreboard.Team team, String teamName) {
		// setting team name
		if (type == BelowNameType.PREFIX) {
			team.setPrefix(teamName);
		} else if (type == BelowNameType.SUFFIX) {
			team.setSuffix(" " + teamName);
		}

		if (!Main.plugin.getConfig().getBoolean("collide")) {
			team.setOption(Option.COLLISION_RULE, OptionStatus.FOR_OWN_TEAM);
		}

		if (Main.plugin.getConfig().getBoolean("privateDeath")) {
			team.setOption(Option.DEATH_MESSAGE_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
		}

		if (Main.plugin.getConfig().getBoolean("privateName")) {
			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		}

		team.setCanSeeFriendlyInvisibles(Main.plugin.getConfig().getBoolean("canSeeFriendlyInvisibles"));

	}

	public enum BelowNameType {
		PREFIX, SUFFIX, FALSE;

		public static BelowNameType getType(String string) {

			switch (string.toLowerCase()) {
				case "prefix":
				case "true":
					return PREFIX;
				case "suffix":
					return SUFFIX;
				default:
					return FALSE;
			}
		}
	}

}
