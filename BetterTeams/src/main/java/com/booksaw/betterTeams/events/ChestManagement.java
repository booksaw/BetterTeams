package com.booksaw.betterTeams.events;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Iterator;

public class ChestManagement implements Listener {

	public static boolean enableClaims = true;

	public static Location getLocation(Chest chest) {
		return new Location(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ());
	}

	/**
	 * Used to get the other side of a DoubleChest
	 *
	 * @param block A Chest (either DoubleChest or Single)
	 * @return The other side Location
	 */
	public static Location getOtherSide(Block block) {
		if (block.getType() != Material.CHEST) return null; // Just in case, should be a light check

		org.bukkit.block.data.type.Chest chest = (org.bukkit.block.data.type.Chest) block.getBlockData();

		if (chest.getType() == org.bukkit.block.data.type.Chest.Type.SINGLE) return block.getLocation();

		if (chest.getType() == org.bukkit.block.data.type.Chest.Type.LEFT) {
			switch (chest.getFacing()) {
				case NORTH:
					return block.getRelative(BlockFace.EAST).getLocation();
				case EAST:
					return block.getRelative(BlockFace.SOUTH).getLocation();
				case SOUTH:
					return block.getRelative(BlockFace.WEST).getLocation();
				case WEST:
					return block.getRelative(BlockFace.NORTH).getLocation();
			}
		} else {
			switch (chest.getFacing()) {
				case NORTH:
					return block.getRelative(BlockFace.WEST).getLocation();
				case EAST:
					return block.getRelative(BlockFace.NORTH).getLocation();
				case SOUTH:
					return block.getRelative(BlockFace.EAST).getLocation();
				case WEST:
					return block.getRelative(BlockFace.SOUTH).getLocation();
			}
		}
		return null;
	}

	/**
	 * Used to check the two locations after they were passed to getOtherSide
	 *
	 * @param l1 One of the two sides
	 * @param l2 One of the two sides
	 * @return
	 */
	public static boolean isSingleChest(Location l1, Location l2) {
		return l1.equals(l2);
	}

	@EventHandler
	public void onOpen(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.CHEST
				|| e.getAction() != Action.RIGHT_CLICK_BLOCK
				|| e.getPlayer().hasPermission("betterteams.chest.bypass")) {
			return;
		}
		Team team = Team.getTeam(e.getPlayer());

		Team claimedBy = Team.getClaimingTeam(e.getClickedBlock());
		if (claimedBy != null && team != claimedBy && (team == null || !claimedBy.isAlly(team))) {
			cancelChestEvent(e, claimedBy);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {

		if (e.getPlayer().hasPermission("betterteams.chest.bypass")) {
			return;
		}

		Team claimedBy = Team.getClaimingTeam(e.getBlock());

		if (claimedBy != null) {
			cancelChestEvent(e, claimedBy);
		}

	}

	@EventHandler
	public void onHopper(InventoryMoveItemEvent e) {
		if (e.getSource().getType() != InventoryType.CHEST) return;

		Location location = e.getSource().getLocation();
		if (location == null) return;

		Team claimedBy = Team.getClaimingTeam(location.getBlock());

		if (claimedBy != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onExplode(BlockExplodeEvent e) {
		Iterator<Block> iter = e.blockList().iterator();
		while (iter.hasNext()) {
			Block b = iter.next();
			Team claimedBy = Team.getClaimingTeam(b);

			if (claimedBy != null) {
				iter.remove();
			}
		}

	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		Iterator<Block> iter = e.blockList().iterator();
		while (iter.hasNext()) {
			Block b = iter.next();
			if (b.getType() != Material.CHEST) {
				continue;
			}
			Team claimedBy = Team.getClaimingTeam(b);

			if (claimedBy != null) {
				iter.remove();
			}
		}
	}

	private void cancelChestEvent(PlayerInteractEvent e, Team claimedBy) {
		// checking if chest claims are currently enabled
		if (enableClaims) {
			MessageManager.sendMessage(e.getPlayer(), "chest.claimed", claimedBy.getName());
			e.setCancelled(true);
		}
	}

	private void cancelChestEvent(BlockBreakEvent e, Team claimedBy) {
		if (enableClaims) {
			MessageManager.sendMessage(e.getPlayer(), "chest.claimed", claimedBy.getName());
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		// used to notifiy players if claimed chests can be opened
		if (!enableClaims)
			MessageManager.sendMessage(e.getPlayer(), "admin.chest.disabled.bc");
	}
}