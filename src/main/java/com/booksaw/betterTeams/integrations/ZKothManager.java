package com.booksaw.betterTeams.integrations;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import fr.maxlego08.zkoth.api.FactionListener;
import fr.maxlego08.zkoth.api.event.events.KothHookEvent;
import fr.maxlego08.zkoth.api.event.events.KothWinEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class ZKothManager implements Listener {

	@EventHandler
	public void onRegister(KothHookEvent event) {
		event.setFactionListener(new TeamListener());
	}

	@EventHandler
	public void onWin(KothWinEvent e) {
		Team team = Team.getTeam(e.getPlayer());
		if (team == null) {
			return;
		}
		// increasing the team score by the configured amount
		team.setScore(team.getScore() + Main.plugin.getConfig().getInt("zkoth.pointsPerCapture"));
	}

	public static class TeamListener implements FactionListener {

		@Override
		public String getFactionTag(Player p) {
			Team team = Team.getTeam(p);
			if (team == null) {
				return null;
			}

			return team.getName();
		}

		@Override
		public List<Player> getOnlinePlayer(Player p) {
			Team team = Team.getTeam(p);
			if (team == null) {
				return null;
			}

			return team.getOnlineMemebers();
		}
	}

}
