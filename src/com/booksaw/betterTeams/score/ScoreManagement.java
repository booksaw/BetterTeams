package com.booksaw.betterTeams.score;

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
import com.booksaw.betterTeams.score.ScoreChange.ChangeType;

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
		if (!purges.isEmpty()) {
			sched();
		}

	}

	/**
	 * This class is used to schedule events
	 */
	private void sched() {
		BukkitScheduler scheduler = Main.plugin.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(Main.plugin, () -> {

			if (purges.get(nextPurge).isNow()) {
				if (run) {
					return;
				}

				run = true;
				Team.getTeamManager().purgeTeams();
				if (nextPurge + 1 < purges.size()) {
					nextPurge++;
				} else {
					nextPurge = 0;
				}
				return;
			}
			// clean pass so it can reset the tracker
			run = false;

		}, 0L, 20 * 60L);
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
		private final int days;
		private final int hours;

		public Date(int date, int hours) {
			this.days = date;
			this.hours = hours;
		}

		/**
		 * @param date the date to check if this date is before
		 * @return if the event is before now
		 */
		public boolean isBefore(Date date) {
			if (date.days < this.days) {
				return false;
			} else if (date.days > this.days) {
				return true;
			} else {
				return date.hours > hours;
			}

		}

		/**
		 * @return if the event is now
		 */
		public boolean isNow() {
			LocalDateTime now = LocalDateTime.now();
			return days == now.getDayOfMonth() && now.getHour() == hours;
		}

		/**
		 * Used to check if the date is after the current time
		 * 
		 * @return
		 */
		public boolean isAfterNow() {
			LocalDateTime now = LocalDateTime.now();
			Date nowDate = new Date(now.getDayOfMonth(), now.getHour());
			return nowDate.isBefore(this);
		}
	}

	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player killed = e.getEntity().getPlayer();
		// score decreases
		Team killedTeam = Team.getTeam(killed);
		if (killedTeam != null) {
			death(killed, killedTeam);
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
		kill(killer, killed, killerTeam, killedTeam);

	}

	public void kill(Player source, Player target, Team souceTeam, Team targetTeam) {

		int scoreForKill;

		if (ScoreChange.isSpam(ChangeType.KILL, source, target)) {
			scoreForKill = Main.plugin.getConfig().getInt("events.kill.spam");

		} else {
			new ScoreChange(ChangeType.KILL, source, target);
			scoreForKill = Main.plugin.getConfig().getInt("events.kill.score");
		}

		if (souceTeam == targetTeam) {
			souceTeam.setScore(souceTeam.getScore() - scoreForKill);
		} else {
			souceTeam.setScore(souceTeam.getScore() + scoreForKill);
		}
	}

	public void death(Player source, Team souceTeam) {

		int scoreForDeath;

		if (ScoreChange.isSpam(ChangeType.DEATH, source)) {
			scoreForDeath = Main.plugin.getConfig().getInt("events.death.spam");

		} else {
			new ScoreChange(ChangeType.DEATH, source);
			scoreForDeath = Main.plugin.getConfig().getInt("events.death.score");
		}

		souceTeam.setScore(souceTeam.getScore() + scoreForDeath);
	}

}
