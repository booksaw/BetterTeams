package com.booksaw.betterTeams.events;

import java.util.ArrayList;
import java.util.List;
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
import com.booksaw.betterTeams.customEvents.BelowNameChangeListener;

public class MCTeamManagement implements Listener {

	private final BelowNameType type;

	Scoreboard board;

	public MCTeamManagement(BelowNameType type) {
		this.type = type;

		if (Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard() != null) {
			board = Bukkit.getScoreboardManager().getMainScoreboard();
		} else {
			board = Bukkit.getScoreboardManager().getNewScoreboard();
		}

	}

	/**
	 * Used to track a list of all listeners
	 * 
	 * @see BelowNameChangeListener
	 */
	private final List<BelowNameChangeListener> listeners = new ArrayList<>();

	/**
	 * @param listener The listener to add to the list of active listeners
	 */
	public void addListener(BelowNameChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener The listener to remove from the list of active listeners
	 */
	public void removeListener(BelowNameChangeListener listener) {
		listeners.remove(listener);
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

		// triggering the listeners
		for (BelowNameChangeListener listener : listeners) {
			listener.run(event);
		}

	}

	/**
	 * Used when the plugin is disabled
	 */
	public void removeAll() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			remove(p);
		}

		for (Entry<UUID, Team> t : Team.getTeamList().entrySet()) {
			org.bukkit.scoreboard.Team team = t.getValue().getScoreboardTeamOrNull();

			if (team != null) {
				team.unregister();
			}

		}
	}

	/**
	 * Used to remove the prefix / suffix from the specified player
	 * 
	 * @param player the player to remove the prefix/suffix from
	 */
	public void remove(Player player) {

		if (player == null) {
			return;
		}

		Team team = Team.getTeam(player);
		if (team == null) {
//			for (BelowNameChangeListener listener : listeners) {
//				listener.run(new BelowNameChangeEvent(player, ChangeType.REMOVE));
//			}
			return;
		}
		team.getScoreboardTeam(board).removeEntry(player.getName());

		BelowNameChangeEvent event = new BelowNameChangeEvent(player, ChangeType.REMOVE);
		Bukkit.getPluginManager().callEvent(event);

		// triggering the listeners
		for (BelowNameChangeListener listener : listeners) {
			listener.run(new BelowNameChangeEvent(player, ChangeType.REMOVE));
		}

	}

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e) {
		displayBelowName(e.getPlayer());
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
			}

			return FALSE;
		}
	}

	public BelowNameType getType() {
		return type;
	}

	public void setupTeam(org.bukkit.scoreboard.Team team, String teamName) {
		// setting team name
		if (type == BelowNameType.PREFIX) {
			team.setPrefix(teamName);
		} else if (type == BelowNameType.SUFFIX) {
			team.setSuffix(teamName);
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

}
