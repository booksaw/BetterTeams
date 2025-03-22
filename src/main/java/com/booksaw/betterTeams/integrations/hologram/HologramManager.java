package com.booksaw.betterTeams.integrations.hologram;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.MessageManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

public abstract class HologramManager {

	public static HologramManager holoManager;

	HashMap<LocalHologram, HologramType> holos;

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
		LocalHologram holo = createLocalHolo(location, type);
		holos.put(holo, type);
		reloadHolo(holo, type);
	}

	/*
	 * Creates a new LocalHologram using the available hologram plugin.
	 */
	public abstract LocalHologram createLocalHolo(Location location, HologramType type);

	public void reloadHolo(LocalHologram holo, HologramType type) {
		String[] teams = getSortedArray(type);
		holo.clearLines();

		int maxHologramLines = Main.plugin.getConfig().getInt("maxHologramLines");

		holo.appendText(MessageManager.getMessage("holo.leaderboard"));
		for (int i = 0; i < maxHologramLines && i < teams.length; i++) {
			Team team = Team.getTeam(teams[i]);
			if (team == null) {
				Main.plugin.getLogger().severe("A team was null for an unexplained reason, team name: " + teams[i]);
				continue;
			}
			holo.appendText(MessageManager.getMessage(type.getSyntaxReference(), team.getName(),
					getValue(type, team)));
		}
	}

	public void startUpdates() {
		BukkitScheduler scheduler = Main.plugin.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(Main.plugin, () -> {
			if (!isHolographicDisplaysEnabled() && !isDecentHologramsEnabled()) {
				return;
			}
			for (HologramType type : HologramType.values()) {
				if (needsUpdating(type)) {
					for (Entry<LocalHologram, HologramType> holo : holos.entrySet()) {
						if (holo.getValue() == type) {
							reloadHolo(holo.getKey(), holo.getValue());
						}
					}
				}
			}
		}, 0L, 20 * 60L);
	}

	private boolean isHolographicDisplaysEnabled() {
		return Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null && Objects
				.requireNonNull(Bukkit.getPluginManager().getPlugin("HolographicDisplays")).isEnabled();
	}

	private boolean isDecentHologramsEnabled() {
		return Bukkit.getPluginManager().getPlugin("DecentHolograms") != null && Objects
				.requireNonNull(Bukkit.getPluginManager().getPlugin("DecentHolograms")).isEnabled();
	}

	public LocalHologram getNearestHologram(Location location) {
		LocalHologram nearest = null;
		double distance = -1;

		for (LocalHologram holo : holos.keySet()) {
			double tempDistance = location.distance(holo.getLocation());
			if (nearest == null || tempDistance < distance) {
				nearest = holo;
				distance = tempDistance;
			}
		}

		return nearest;
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
		for (Entry<LocalHologram, HologramType> holo : holos.entrySet()) {
			holostr.add(getString(holo.getKey().getLocation()) + ";" + holo.getValue());
			holo.getKey().delete();
		}
		Team.getTeamManager().setHoloDetails(holostr);
		holos = new HashMap<>();
	}

	public void removeHolo(LocalHologram toRemove) {
		toRemove.delete();
		holos.remove(toRemove);
	}

	@Getter
	@RequiredArgsConstructor
	public enum HologramType {
		MONEY("holo.msyntax"), SCORE("holo.syntax");

		private final String syntaxReference;
	}

	public interface LocalHologram {
		/**
		 * Appends a new line of text to the hologram.
		 */
		void appendText(String text);

		/**
		 * Clears all lines of text from the hologram.
		 */
		void clearLines();

		/**
		 * Removes the hologram from the world.
		 */
		void delete();

		/**
		 * Returns the location of the hologram.
		 */
		Location getLocation();
	}

}
