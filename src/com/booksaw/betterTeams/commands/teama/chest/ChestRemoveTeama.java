package com.booksaw.betterTeams.commands.teama.chest;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;

public class ChestRemoveTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Player player = (Player) sender;
		Location loc = player.getLocation();

		Block block = loc.getBlock();
		loc = Team.getClaimingLocation(block);

		if (block == null || block.getType() != Material.CHEST) {
			return new CommandResponse("chest.remove.noChest");
		}

		Team team = Team.getClamingTeam(loc);

		if (loc == null || team == null) {
			return new CommandResponse("chest.remove.notClaimed");
		}

		// they can claim the chest
		team.removeClaim(loc);

		return new CommandResponse(true, "admin.chest.remove.success");
	}

	@Override
	public String getCommand() {
		return "remove";
	}

	@Override
	public String getNode() {
		return "admin.chest.remove";
	}

	@Override
	public String getHelp() {
		return "Remove the claim from the chest you are standing on";
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
	public boolean needPlayer() {
		return true;
	}

}
