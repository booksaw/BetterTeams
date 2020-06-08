package com.booksaw.betterTeams.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.booksaw.betterTeams.Team;

public class ScoreManagement implements Listener {

	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		if (e.getEntity().getKiller() == null || !(e.getEntity() instanceof Player)
				|| !(e.getEntity().getKiller() instanceof Player)) {
			return;
		}
		Player killed = e.getEntity().getPlayer();
		Player killer = e.getEntity().getKiller().getPlayer();

		Team killerTeam = Team.getTeam(killer);
		if (killerTeam == null) {
			return;
		}

		Team killedTeam = Team.getTeam(killed);

		if (killerTeam == killedTeam) {
			killerTeam.setScore(killerTeam.getScore() - 1);
		} else {
			killerTeam.setScore(killerTeam.getScore() + 1);
		}
	}

}
