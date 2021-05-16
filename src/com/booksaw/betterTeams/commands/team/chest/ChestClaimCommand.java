package com.booksaw.betterTeams.commands.team.chest;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.team.LocationListComponent;

public class ChestClaimCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		Location loc = player.getPlayer().getPlayer().getLocation();

		Block block = loc.getBlock();
		loc = LocationListComponent.normalise(loc);

		if (block == null || block.getType() != Material.CHEST) {
			return new CommandResponse("chest.claim.noChest");
		}

		Team claimedBy = Team.getClaimingTeam(block);
		if (claimedBy != null) {
			return new CommandResponse("chest.claim.claimed");
		}

		// on a chest
		// checking there is no limit on chests
		int lim = Main.plugin.getConfig().getInt("levels.l" + team.getLevel() + ".maxChests");
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

}
