package com.booksaw.betterTeams.commands.team.chest;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.team.LocationSetComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;


public class ChestClaimCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		Location loc = Objects.requireNonNull(player.getPlayer().getPlayer()).getLocation();

		Block block = loc.getBlock();
		loc = LocationSetComponent.normalise(loc);

		if (block.getType() != Material.CHEST) {
			return new CommandResponse("chest.claim.noChest");
		}

		Team claimedBy = Team.getClaimingTeam(block);
		if (claimedBy != null) {
			return new CommandResponse("chest.claim.claimed");
		}

		// on a chest
		// checking there is no limit on chests
		int lim = team.getMaxChests();
		if (lim != -1 && lim <= team.getClaimCount()) {
			return new CommandResponse("chest.claim.limit");
		}

		// they can claim the chest
		team.addClaim(loc);

		return new CommandResponse(true, "chest.claim.success");
	}

	@Override
	public String getCommand() {
		return "claim";
	}

	@Override
	public String getNode() {
		return "chest.claim";
	}

	@Override
	public String getHelp() {
		return "Claim the chest you are standing on";
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
	public PlayerRank getDefaultRank() {
		return PlayerRank.ADMIN;
	}

	@Override
	public boolean runAsync(String[] args) {
		return false;
	}

}
