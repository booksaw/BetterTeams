package com.booksaw.betterTeams.util;

import io.papermc.lib.PaperLib;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Adapter {
	private static final boolean isHolderSupported = PaperLib.isPaper() && PaperLib.getEnvironment().isVersion(16);

	public static InventoryHolder getInventoryHolder(Inventory inventory, boolean snapshots) {
		return PaperLib.getHolder(inventory, snapshots).getHolder();
	}

	public static InventoryHolder getLeftSide(DoubleChest chest, boolean snapshots) {
		if (isHolderSupported) {
			return chest.getLeftSide(false);
		}
		return chest.getLeftSide();
	}

	public static InventoryHolder getRightSide(DoubleChest chest, boolean snapshots) {
		if (isHolderSupported) {
			return chest.getRightSide(false);
		}
		return chest.getRightSide();
	}

	public static BlockState getState(Block block, boolean snapshots) {
		return PaperLib.getBlockState(block, snapshots).getState();
	}
}
