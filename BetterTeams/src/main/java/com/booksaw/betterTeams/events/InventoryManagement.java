package com.booksaw.betterTeams.events;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.team.EChestComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;

public class InventoryManagement implements Listener {

	public static final Map<Player, Team> adminViewers = new HashMap<>();

	private final String inventoryName;

	public InventoryManagement() {
		inventoryName = MessageManager.getMessage("echest.echest");
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {

		if (e.getView().getType() != InventoryType.CHEST || !e.getView().getTitle().equals(inventoryName) || (!(e.getInventory().getHolder() instanceof EChestComponent.EchestInventoryHolder))) {
			e.getInventory().getHolder();
			return;
		}
		Team t = adminViewers.get((Player) e.getPlayer());

		if (t == null) {
			t = Team.getTeam((Player) e.getPlayer());
			if (t == null) {
				return;
			}
		}

		adminViewers.remove((Player) e.getPlayer());
		t.saveEchest();

	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getType() != InventoryType.CHEST || !e.getView().getTitle().equals(inventoryName) || (!(e.getInventory().getHolder() instanceof EChestComponent.EchestInventoryHolder))) {
			e.getInventory().getHolder();
			return;
		}
		if (!(e.getWhoClicked() instanceof Player p)) {
			return;
		}

		if (p.hasPermission("betterteams.admin.echest")) {
			return; // player has /teama echest permission, they may be viewing correctly
		}

		// Inventory clicked is a BetterTeams inventory and user is a player
		Team team = Team.getTeam(p);
		if (team != null) {
			return; // player is in a team and likely meant to be interacting with the inventory
		}

		// player is not in a team, they should not be in a betterteams inventory
		Main.plugin.getLogger().warning("Player " + p.getName() + " is attempting to exploit BetterTeams echests to dupe items!!!");
		e.setCancelled(true);
		p.closeInventory();
	}

}
