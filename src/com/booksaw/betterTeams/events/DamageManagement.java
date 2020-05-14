package com.booksaw.betterTeams.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.booksaw.betterTeams.Team;

/**
 * This class is used to ensure that members of the same team cannot hit each
 * other
 * 
 * @author booksaw
 *
 */
public class DamageManagement implements Listener {

	/**
	 * This is used to cancel any events which would cause 2 players of the same
	 * team to damage each other
	 * 
	 * @param e the damage event
	 */
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {

		if (e.isCancelled()) {
			return;
		}

		if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
			return;
		}
		Team temp = Team.getTeam((Player) e.getEntity());
		if (temp != null && temp == Team.getTeam((Player) e.getDamager())) {
			// they are on the same team
			e.setCancelled(true);
		}
	}

}
