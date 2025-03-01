package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public abstract class LocationSetComponent extends SetTeamComponent<Location> {

	/**
	 * This method is used to convert a string into a location which can be stored
	 * for later use
	 *
	 * @param loc the string to convert into a location
	 * @return the location which that string reference
	 */
	public static Location getLocation(String loc) {
		String[] split = loc.split(":");

		if (split.length < 6) {
			throw new IllegalArgumentException("Invalid location string: " + loc);
		}

		return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]),
				Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
	}

	/**
	 * This method is used to convert a location into a string which can be stored
	 * in a configuration file
	 *
	 * @param loc the location to convert into a string
	 * @return the string which references that location
	 */
	public static String getString(Location loc) {
		return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw()
				+ ":" + loc.getPitch();
	}

	/**
	 * Normalise the location to the 0,0,0 coords of the current block
	 *
	 * @param loc The location to normalise
	 * @return The normalised location
	 */
	public static Location normalise(Location loc) {
		return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	@Override
	public Location fromString(String str) {
		return getLocation(str);
	}

	@Override
	public String toString(Location component) {
		return getString(component);
	}

	@Override
	public void remove(Team team, Location component) {
		for (Location loc : getClone()) {
			if (loc.equals(component)) {
				set.remove(loc);
				return;
			}
		}
	}

	@Override
	public boolean contains(Location component) {
		for (Location loc : getClone()) {
			if (loc.equals(component)) {
				return true;
			}
		}
		return false;
	}

}
