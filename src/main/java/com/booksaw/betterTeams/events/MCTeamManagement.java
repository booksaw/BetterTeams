package com.booksaw.betterTeams.events;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.BelowNameChangeEvent;
import com.booksaw.betterTeams.customEvents.BelowNameChangeEvent.ChangeType;

public class MCTeamManagement implements Listener {

	final Scoreboard board;
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

		team.getScoreboardTeam(board).addEntry(player.getName());

	}

	/**
	 * Used when the plugin is disabled
	 */
	public void removeAll() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			remove(p, false);
		}

		// only loaded teams will have a team manager
		for (Entry<UUID, Team> t : Team.getTeamManager().getLoadedTeamListClone().entrySet()) {
			org.bukkit.scoreboard.Team team = t.getValue().getScoreboardTeamOrNull();

			if (team != null) {
				team.unregister();
			}

		}
	}

	/**
	 * Remove a player from an async thread
	 * 
	 * @param player
	 */
	public void remove(Player player) {
		remove(player, true);
	}

	/**
	 * Used to remove the prefix / suffix from the specified player
	 *
	 * @param player  the player to remove the prefix/suffix from
	 * @param isAsync if the method is being run async or not
	 */
	public void remove(Player player, boolean isAsync) {

		if (player == null) {
			return;
		}

		Team team = Team.getTeam(player);
		if (team == null) {
			return;
		}
		team.getScoreboardTeam(board).removeEntry(player.getName());

		BelowNameChangeEvent event = new BelowNameChangeEvent(player, ChangeType.REMOVE, isAsync);
		Bukkit.getPluginManager().callEvent(event);

	}

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
			displayBelowName(e.getPlayer());
		});
	}

	public BelowNameType getType() {
		return type;
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
