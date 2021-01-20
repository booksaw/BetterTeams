package com.booksaw.betterTeams.events;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.PrePurgeEvent;

import me.clip.placeholderapi.PlaceholderAPI;

public class ScoreManagement implements Listener {

	private int nextPurge;
	private final List<Date> purges;
	private boolean run = false;

	public ScoreManagement() {
		purges = new ArrayList<>();
		nextPurge = -1;

		Main.plugin.getConfig().getStringList("autoPurge").forEach(str -> {
			String[] split = str.split(":");
			Date temp = new Date(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
			purges.add(temp);
			if (nextPurge == -1 && temp.isAfterNow()) {
				nextPurge = purges.indexOf(temp);
			}

		});
		run = true;

		if (nextPurge == -1) {
			nextPurge = 0;
		}

		// if there are actually purges
		if (purges.size() != 0) {
			sched();
		}

	}

	/**
	 * This class is used to schedule events
	 */
	private void sched() {
		BukkitScheduler scheduler = Main.plugin.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

			@Override
			public void run() {
				if (purges.get(nextPurge).isNow()) {
					if (run) {
						return;
					}

					run = true;
					Team.purge();
					if (nextPurge + 1 < purges.size()) {
						nextPurge++;
					} else {
						nextPurge = 0;
					}
					return;
				}
				// clean pass so it can reset the tracker
				run = false;
			}

		}, 0L, 20 * 60L);
	}

	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player killed = e.getEntity().getPlayer();
		// score decreases
		Team killedTeam = Team.getTeam(killed);
		if (killedTeam != null) {
			killedTeam.setScore(killedTeam.getScore() - Main.plugin.getConfig().getInt("pointsLostByDeath"));
		}

		if (e.getEntity().getKiller() == null || !(e.getEntity() instanceof Player)
				|| !(e.getEntity().getKiller() instanceof Player)) {
			return;
		}

		Player killer = e.getEntity().getKiller().getPlayer();

		Team killerTeam = Team.getTeam(killer);
		if (killerTeam == null) {
			return;
		}

		int scoreForKill = Main.plugin.getConfig().getInt("scoreForKill");
		
		if (killerTeam == killedTeam) {
			killerTeam.setScore(killerTeam.getScore() - scoreForKill);
		} else {
			killerTeam.setScore(killerTeam.getScore() + scoreForKill);
		}
	}

	@EventHandler
	public void onPurge(PrePurgeEvent e) {
		Main.plugin.getConfig().getStringList("purgeCommands").forEach(cmd -> {
			if (Main.plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
				cmd = PlaceholderAPI.setPlaceholders(null, cmd);
			}
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		});
	}

	private class Date {
		private final int date, hours;

		public Date(int date, int hours) {
			this.date = date;
			this.hours = hours;
		}

//		public int getDate() {
//			return date;
//		}
//
//		public int getHours() {
//			return hours;
//		}

		/**
		 * @param date the date to check if this date is before
		 * @return if the event is before now
		 */
		public boolean isBefore(Date date) {
			if (date.date < this.date) {
				return false;
			} else if (date.date > this.date) {
				return true;
			} else {
				if (date.hours <= hours) {
					return false;
				} else {
					return true;
				}
			}

		}

		/**
		 * @return if the event is now
		 */
		public boolean isNow() {
			LocalDateTime now = LocalDateTime.now();
			if (date == now.getDayOfMonth() && now.getHour() == hours) {
				return true;
			}
			return false;
		}

		/**
		 * Used to check if hte date is after the current time
		 * 
		 * @return
		 */
		public boolean isAfterNow() {
			LocalDateTime now = LocalDateTime.now();
			Date nowDate = new Date(now.getDayOfMonth(), now.getHour());
			return nowDate.isBefore(this);
		}
	}

}
