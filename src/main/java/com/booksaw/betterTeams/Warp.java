package com.booksaw.betterTeams;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.team.LocationSetComponent;

public class Warp {

	private final Location location;
	private final String name;
	private final String password;

	public Warp(String[] args) {
		name = args[0];
		location = LocationSetComponent.getLocation(args[1]);

		if (args.length == 3) {
			password = args[2];
		} else {
			password = null;
		}

	}

	public Warp(String name, Location location, String password) {
		this.name = name;
		this.location = location;
		this.password = password;
	}

	public Location getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		if (password == null || password.isEmpty()) {
			return name + ";" + LocationSetComponent.getString(location);
		} else {
			return name + ";" + LocationSetComponent.getString(location) + ";" + password;
		}
	}

	public void execute(Player player) {
		try {
			new PlayerTeleport(player, location, "warp.success");
		} catch (Exception e) {
			throw new NullPointerException();
		}
	}

}
