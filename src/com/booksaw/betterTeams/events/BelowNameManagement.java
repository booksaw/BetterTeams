package com.booksaw.betterTeams.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.BelowNameChangeEvent;
import com.booksaw.betterTeams.customEvents.BelowNameChangeEvent.ChangeType;
import com.booksaw.betterTeams.customEvents.BelowNameChangeListener;

public class BelowNameManagement implements Listener {

	private BelowNameType type;

	Scoreboard board;

	public BelowNameManagement(BelowNameType type) {
		this.type = type;

		if (Bukkit.getScoreboardManager().getMainScoreboard() != null) {
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
	private List<BelowNameChangeListener> listeners = new ArrayList<>();

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

		team.getScoreboardTeam(board, type).addEntry(player.getName());

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
	}

	/**
	 * Used to remove the prefix / suffix from the specified player
	 * 
	 * @param player the player to remove the prefix/suffix from
	 */
	public void remove(Player player) {

		Team team = Team.getTeam(player);
		if (team == null) {
			for (BelowNameChangeListener listener : listeners) {
				listener.run(new BelowNameChangeEvent(player, ChangeType.REMOVE));
			}
			return;
		}

		team.getScoreboardTeam(board, type).removeEntry(player.getName());

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

}
