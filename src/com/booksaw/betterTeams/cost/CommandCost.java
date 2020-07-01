package com.booksaw.betterTeams.cost;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Main;

import net.milkbowl.vault.economy.EconomyResponse;

public class CommandCost {

	private double cost;
	private String command;

	/**
	 * Used within Cost manager to track a new commands Cost
	 * 
	 * @param command the reference for the command
	 * @param Cost    how long of a Cost that command has (in seconds)
	 */
	public CommandCost(String command, double Cost) {
		this.command = command;
		this.cost = Cost;
	}

	/**
	 * Run when a player runs the command to subtract the cost of the command
	 * 
	 * @param player the player to add a Cost for
	 * @return If the player can run the command (if the transaction is a success or
	 *         failure)
	 */
	public boolean runCommand(Player player) {
		if (player.hasPermission("betterteams.cost.bypass")) {
			return true;
		}

		if (Main.econ == null) {
			Bukkit.getLogger().warning("Could not detect vault, command running with no cost");
			return true;
		}
		EconomyResponse response = Main.econ.withdrawPlayer(player, cost);
		return response.transactionSuccess();
	}

	public String getCommand() {
		return command;
	}

	public double getCost() {
		return cost;
	}

	/**
	 * 
	 * @param player the player to check for
	 * @return if a player has the money to run that command (to check before
	 *         executing the command)
	 */
	public boolean hasBalance(Player player) {
		if (player.hasPermission("betterteams.cost.bypass")) {
			return true;
		}

		if (Main.econ == null) {
			Bukkit.getLogger().warning("Could not detect vault, command running with no cost");
			return true;
		}

		if (Main.econ.has(player, cost)) {
			return true;
		}

		return false;
	}

}
