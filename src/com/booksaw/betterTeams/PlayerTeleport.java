package com.booksaw.betterTeams;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.booksaw.betterTeams.message.MessageManager;

/**
 * A class to handle a teleport with a delay
 * 
 * @author booksaw
 *
 */
public class PlayerTeleport {

	private final Player player;
	private final Location location;
	private final Location playerLoc;
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

		this.playerLoc = player.getLocation();

		if (player.hasPermission("betterteams.warmup.bypass")) {
			runTp();
			return;
		}

		int wait = Main.plugin.getConfig().getInt("tpDelay");
		if (wait <= 0) {
			runTp();
			return;
		}

		// sending the wait message
		MessageManager.sendMessageF(player, "teleport.wait", wait + "");

		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
			if (canTp()) {
				try {
					runTp();
				} catch (Exception e) {
					throw new NullPointerException();
				}
			} else {
				cancel();
			}
		}, 20L * wait);

	}

	public void runTp() {
		if (location == null || location.getWorld() == null) {
			throw new NullPointerException();
		}

		PlayerTeleportEvent event = new PlayerTeleportEvent(player, player.getLocation(), location);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		player.teleport(location);
		MessageManager.sendMessage(player, reference);
	}

	public boolean canTp() {

		if (!Main.plugin.getConfig().getBoolean("noMove")) {
			return true;
		}

		return playerLoc.distance(player.getLocation()) <= Math.abs(Main.plugin.getConfig().getInt("maxMove"));
	}

	public void cancel() {
		MessageManager.sendMessage(player, "teleport.fail");
	}

}
