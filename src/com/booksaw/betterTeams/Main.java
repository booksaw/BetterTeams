package com.booksaw.betterTeams;

import org.bukkit.plugin.java.JavaPlugin;

import com.booksaw.betterTeams.commands.CommandTeam;

public class Main extends JavaPlugin {

	public static Main pl;

	@Override
	public void onEnable() {

		saveDefaultConfig();
		pl = this;

		getCommand("team").setExecutor(new CommandTeam());

	}

}
