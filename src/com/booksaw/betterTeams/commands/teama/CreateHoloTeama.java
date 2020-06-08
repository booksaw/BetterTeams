package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.MessageManager;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class CreateHoloTeama extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;

		Hologram holo = HologramsAPI.createHologram(Main.plugin, p.getLocation());
		Team[] teams = Team.sortTeams();

		int maxHologramLines = Main.plugin.getConfig().getInt("maxHologramLines");

		holo.appendTextLine(MessageManager.getMessage("holo.leaderboard"));

		for (int i = 0; i < maxHologramLines && i < teams.length; i++) {
			holo.appendTextLine(
					String.format(MessageManager.getMessage("holo.syntax"), teams[i].getName(), teams[i].getScore()));
		}

		return "admin.holo.create.success";
	}

	@Override
	public String getCommand() {
		return "create";
	}

	@Override
	public String getNode() {
		return "admin.holo";
	}

	@Override
	public String getHelp() {
		return "Create a hologram at your current location";
	}

	@Override
	public String getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinimumArguments() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		// TODO
	}

	@Override
	public boolean needPlayer() {
		return true;
	}

}
