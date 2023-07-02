package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.integrations.hologram.HologramManager;
import com.booksaw.betterTeams.integrations.hologram.HologramManager.LocalHologram;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveHoloTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		LocalHologram closest = HologramManager.holoManager.getNearestHologram(p.getLocation());

		if (closest == null) {
			return new CommandResponse("admin.holo.remove.noHolo");
		}

		HologramManager.holoManager.removeHolo(closest);
		return new CommandResponse(true, "admin.holo.remove.success");
	}

	@Override
	public String getCommand() {
		return "remove";
	}

	@Override
	public String getNode() {
		return "admin.holo";
	}

	@Override
	public String getHelp() {
		return "Remove the leaderboard hologram closest to you";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}
	
	@Override
	public boolean runAsync(String[] args) {
		return false;
	}

}
