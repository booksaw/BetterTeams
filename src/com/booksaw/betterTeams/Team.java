package com.booksaw.betterTeams;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Team {

	// ID is used for internal processing.
	UUID ID;
	String name;
	String description;
	boolean open;
	Location teamHome;

	List<TeamPlayer> members;

	public Team(UUID ID) {

		this.ID = ID;

		FileConfiguration config = Main.pl.getConfig();

		name = getString(config, "name");
		description = getString(config, "description");
		open = getBoolean(config, "open");

	}

	public String getString(FileConfiguration config, String attribute) {
		return config.getString("team." + ID + "." + attribute);
	}

	public boolean getBoolean(FileConfiguration config, String attribute) {
		return config.getBoolean("team." + ID + "." + attribute);
	}

}
