package com.booksaw.betterTeams.team;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.booksaw.betterTeams.message.MessageManager;

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
	public void load(ConfigurationSection section) {

		for (int i = 0; i < 27; i++) {
			ItemStack is = section.getItemStack("echest." + i);
			if (is != null) {
				inventory.setItem(i, is);
			}
		}
	}

	@Override
	public void save(ConfigurationSection section) {
		for (int i = 0; i < 27; i++) {

			if (inventory.getItem(i) != null) {
				section.set("echest." + i, inventory.getItem(i));
			}
		}
	}

}
