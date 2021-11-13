package com.booksaw.betterTeams.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.MessageManager;

public class InventoryManagement implements Listener {

	public static final Map<Player, Team> adminViewers = new HashMap<>();

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Team t = adminViewers.get((Player) e.getPlayer());

		if (t == null) {
			t = Team.getTeam((Player) e.getPlayer());
			if (t == null) {
				return;
			}
		}

		if (!e.getView().getTitle().equals(MessageManager.getMessage("echest.echest"))) {
			return;
		}

		adminViewers.remove((Player) e.getPlayer());

		t.saveEchest();

	}

}
