package com.booksaw.betterTeams;

import com.booksaw.betterTeams.team.LocationSetComponent;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Getter
public class Warp {

	private final Location location;
	private final String name;
	private final String encrypPassword;

	public Warp(String[] args) {
		name = args[0];
		location = LocationSetComponent.getLocation(args[1]);

		if (args.length == 3) {
			encrypPassword = hashString(args[2]);
		} else if (args.length == 4) {
			encrypPassword = args[2];
		} else {
			encrypPassword = null;
		}

	}

	public Warp(String name, Location location, String password) {
		this.name = name;
		this.location = location;
		if (password != null && !password.isEmpty()) {
			this.encrypPassword = hashString(password);
		} else {
			this.encrypPassword = null;
		}
	}

	private String hashString(String password) {
		MessageDigest md = null;
		try {
			byte[] bytesOfMessage = password.getBytes(StandardCharsets.UTF_8);
			md = MessageDigest.getInstance("MD5");
			byte[] theMD5digest = md.digest(bytesOfMessage);
			return Arrays.toString(theMD5digest);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Password could not be hashed", e);
		}
	}

	public boolean hasPassword() {
		return encrypPassword != null && !encrypPassword.isEmpty();
	}

	public boolean isCorrectPassword(String password) {
		return encrypPassword.equals(hashString(password)) || !Main.plugin.getConfig().getBoolean("allowPassword");
	}

	@Override
	public String toString() {
		String warpString = LocationSetComponent.getString(location);
		if (warpString == null) {
			return null;
		}

		if (encrypPassword == null || encrypPassword.isEmpty()) {
			return name + ";" + warpString;
		} else {
			return name + ";" + warpString + ";" + encrypPassword + ";E";
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
