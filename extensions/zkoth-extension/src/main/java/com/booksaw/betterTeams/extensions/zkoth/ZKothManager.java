package com.booksaw.betterTeams.extensions.zkoth;

import com.booksaw.betterTeams.Team;
import fr.maxlego08.koth.api.events.KothWinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class ZKothManager implements Listener {

	private final int pointsPerCapture;

	public ZKothManager(int pointsPerCapture) {
		this.pointsPerCapture = pointsPerCapture;
	}

	@SuppressWarnings("all")
	@EventHandler
	public void onWin(KothWinEvent e) {
		Team team = Team.getTeam(e.getPlayer());
		if (team == null) {
			return;
		}

		// Use the value passed in the constructor
		team.setScore(team.getScore() + this.pointsPerCapture);
	}
}