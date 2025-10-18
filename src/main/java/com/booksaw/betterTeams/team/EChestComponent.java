package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class EChestComponent implements TeamComponent<Inventory> {

	/**
	 * Inventory holder class given to all Echests controlled by the plugin
	 */
	public class EchestInventoryHolder implements InventoryHolder {
		@NotNull
		@Override
		public Inventory getInventory() {
			return null;
		}
	}

	private Inventory inventory;

	public EChestComponent() {
		inventory = Bukkit.createInventory(new EchestInventoryHolder(), 27, MessageManager.getMessage("echest.echest"));
	}

	@Override
	public Inventory get() {
		return inventory;
	}

	@Override
	public void set(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public void load(TeamStorage section) {
		section.getEchestContents(inventory);
	}

	@Override
	public void save(TeamStorage section) {
		section.setEchestContents(inventory);
	}

}
