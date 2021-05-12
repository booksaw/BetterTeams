package com.booksaw.betterTeams.integrations;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;

import fr.maxlego08.koth.FactionListener;
import fr.maxlego08.koth.event.KothRegisterEvent;
import fr.maxlego08.koth.event.KothWinEvent;

public class ZKothManager implements Listener {

	@EventHandler
	public void onRegister(KothRegisterEvent event) {
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
		public String getFactionTag(String p) {
			Team team = Team.getTeam(Bukkit.getPlayer(p));
			if (team == null) {
				return null;
			}

			return team.getName();
		}

		@Override
		public List<Player> getOnlinePlayer(String p) {
			Team team = Team.getTeam(Bukkit.getPlayer(p));
			if (team == null) {
				return null;
			}

			return team.getOnlineMemebers();
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
