package com.booksaw.betterTeams.integrations;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import fr.maxlego08.koth.api.events.KothWinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ZKothManager implements Listener {

	@EventHandler
	public void onWin(KothWinEvent e) {
		Team team = Team.getTeam(e.getPlayer());
		if (team == null) {
			return;
		}
		// increasing the team score by the configured amount
		team.setScore(team.getScore() + Main.plugin.getConfig().getInt("zkoth.pointsPerCapture"));
	}
}
