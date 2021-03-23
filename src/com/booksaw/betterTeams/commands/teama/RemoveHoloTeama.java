package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.integrations.HologramManager;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class RemoveHoloTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		Hologram closest = null;
		double distance = -1;

		for (Hologram holo : HologramsAPI.getHolograms(Main.plugin)) {
			double tempDistance = p.getLocation().distance(holo.getLocation());
			if (closest == null || tempDistance < distance) {
				closest = holo;
				distance = tempDistance;
			}
		}

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

}
