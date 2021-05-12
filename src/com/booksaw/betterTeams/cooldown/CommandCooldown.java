package com.booksaw.betterTeams.cooldown;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class CommandCooldown {

	final HashMap<Player, Long> nextTime;

	private final int cooldown;
	private final String command;

	/**
	 * Used within cooldown manager to track a new commands cooldown
	 * 
	 * @param command  the reference for the command
	 * @param cooldown how long of a cooldown that command has (in seconds)
	 */
	public CommandCooldown(String command, int cooldown) {
		this.command = command;
		this.cooldown = cooldown * 1000;
		nextTime = new HashMap<>();
	}

	/**
	 * Run when a player runs the command to track when they can next run the
	 * command
	 * 
	 * @param player the player to add a cooldown for
	 */
	public void runCommand(Player player) {
		if (player.hasPermission("betterteams.cooldown.bypass")) {
			return;
		}
		nextTime.put(player, System.currentTimeMillis() + cooldown);
	}

	/**
	 * Used to get how long a player has remaining on the cooldown (to the nearest
	 * second)
	 * 
	 * @param player the player to test the cooldown length for
	 * @return how long they have remaining (returns -1 if they can run the command)
	 */
	public int getRemaining(Player player) {

		if (player.hasPermission("betterteams.cooldown.bypass")) {
			return -1;
		}

		Long end = nextTime.get(player);

		if (end == null) {
			return -1;
		}

		if (end < System.currentTimeMillis()) {
			nextTime.remove(player);
			return -1;
		}

		return (int) ((end - System.currentTimeMillis()) / 1000);
	}

	public int getCooldown() {
		return cooldown;
	}

	public String getCommand() {
		return command;
	}

}
