package com.booksaw.betterTeams.integrations;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.MessageManager;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

public class HologramManager {

	public static HologramManager holoManager;

	HashMap<Hologram, HologramType> holos;

	public HologramManager() {
		holoManager = this;
		holos = new HashMap<>();

		List<String> stored = Team.getTeamManager().getHoloDetails();
		for (String temp : stored) {
			String[] split = temp.split(";");
			if (split.length == 1) {
				createHolo(getLocation(split[0]), HologramType.SCORE);
			} else {
				createHolo(getLocation(split[0]), HologramType.valueOf(split[1]));
			}
		}

		startUpdates();
	}

	// returns the location of a string
	public static Location getLocation(String loc) {
		String[] split = loc.split(":");
		return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]),
				Double.parseDouble(split[3]));
	}

	// returns the string of a location
	public static String getString(Location loc) {
		return Objects.requireNonNull(loc.getWorld()).getName() + ":" + loc.getX() + ":" + loc.getY() + ":"
				+ loc.getZ();
	}

	public void createHolo(Location location, HologramType type) {
		Hologram holo = HologramsAPI.createHologram(Main.plugin, location);
		holos.put(holo, type);
		reloadHolo(holo, type);
	}

	public void reloadHolo(Hologram holo, HologramType type) {
		String[] teams = getSortedArray(type);
		holo.clearLines();

		int maxHologramLines = Main.plugin.getConfig().getInt("maxHologramLines");

		holo.appendTextLine(MessageManager.getMessage("holo.leaderboard"));

		for (int i = 0; i < maxHologramLines && i < teams.length; i++) {
			Team team = Team.getTeam(teams[i]);
			if (team == null) {
				Bukkit.getLogger().severe("A team was null for an unexplained reason, team name: " + teams[i]);
				continue;
			}
			holo.appendTextLine(String.format(MessageManager.getMessage(type.getSyntaxReference()), team.getName(),
					getValue(type, team)));
		}

	}

	public void startUpdates() {
		BukkitScheduler scheduler = Main.plugin.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(Main.plugin, () -> {
			if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null || !Objects
					.requireNonNull(Bukkit.getPluginManager().getPlugin("HolographicDisplays")).isEnabled()) {
				return;
			}
			for (HologramType type : HologramType.values()) {
				if (needsUpdating(type)) {
					for (Entry<Hologram, HologramType> holo : holos.entrySet()) {
						if (holo.getValue() == type) {
							reloadHolo(holo.getKey(), holo.getValue());
						}
					}
				}
			}
		}, 0L, 20 * 60L);
	}

	public String getValue(HologramType type, Team team) {
		switch (type) {
		case MONEY:
			return team.getBalance();
		case SCORE:
			return team.getScore() + "";
		default:
			return "";
		}
	}

	public String[] getSortedArray(HologramType type) {
		if (type == HologramType.MONEY) {
			return Team.getTeamManager().sortTeamsByBalance();
		} else {
			return Team.getTeamManager().sortTeamsByScore();
		}

	}

	public boolean needsUpdating(HologramType type) {
		// TODO reinstate this method
//		switch (type) {
//		case MONEY:
//			return Team.moneyChanges;
//		case SCORE:
//			return Team.scoreChanges;
//		}

		return true;
	}

	public void disable() {
		List<String> holostr = new ArrayList<>();
		for (Entry<Hologram, HologramType> holo : holos.entrySet()) {
			holostr.add(getString(holo.getKey().getLocation()) + ";" + holo.getValue());
			holo.getKey().delete();
		}
		Team.getTeamManager().setHoloDetails(holostr);
		holos = new HashMap<>();
	}

	public void removeHolo(Hologram toRemove) {
		toRemove.delete();
		holos.remove(toRemove);
	}

	public enum HologramType {
		MONEY("holo.msyntax"), SCORE("holo.syntax");

		private final String syntaxReference;

		HologramType(String syntaxReference) {
			this.syntaxReference = syntaxReference;
		}

		public String getSyntaxReference() {
			return syntaxReference;
		}
	}

}
