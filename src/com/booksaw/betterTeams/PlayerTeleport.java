package com.booksaw.betterTeams;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.message.MessageManager;

/**
 * A class to handle a teleport with a delay
 * 
 * @author booksaw
 *
 */
public class PlayerTeleport {

	private Player player;
	private Location location, playerLoc;
	String reference;

	/**
	 * This will start the cooldown (there is no delay)
	 * 
	 * @param player    The player to teleport
	 * @param location  the location to teleport them to
	 * @param reference the reference for the message that should be sent when the
	 *                  player is teleported
	 */
	public PlayerTeleport(Player player, Location location, String reference) {

		this.player = player;
		this.location = location;
		this.reference = reference;

		if (player.hasPermission("betterteams.warmup.bypass")) {
			runTp();
			return;
		}

		int wait = Main.plugin.getConfig().getInt("tpDelay");
		if (wait <= 0) {
			runTp();
			return;
		}

		playerLoc = player.getLocation();
		// sending the wait message
		MessageManager.sendMessageF(player, "teleport.wait", wait + "");

		Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {
			@Override
			public void run() {
				if (canTp()) {
					runTp();
				} else {
					cancel();
				}
			}
		}, 20L * wait);

	}

	public void runTp() {
		player.teleport(location);
		MessageManager.sendMessage(player, reference);
	}

	public boolean canTp() {

		if (!Main.plugin.getConfig().getBoolean("noMove")) {
			return true;
		}

		if (playerLoc.getX() == player.getLocation().getX() && playerLoc.getY() == player.getLocation().getY()
				&& playerLoc.getZ() == player.getLocation().getZ()) {
			return true;
		}

		return false;
	}

	public void cancel() {
		MessageManager.sendMessage(player, "teleport.fail");
	}

}
