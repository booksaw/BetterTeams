package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class EChestComponent implements TeamComponent<Inventory> {

	private Inventory inventory;

	public EChestComponent() {
		inventory = Bukkit.createInventory(null, 27, MessageManager.getMessage("echest.echest"));
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
