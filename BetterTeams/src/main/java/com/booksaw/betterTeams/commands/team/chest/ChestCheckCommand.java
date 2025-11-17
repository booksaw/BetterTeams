package com.booksaw.betterTeams.commands.team.chest;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.team.LocationSetComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class ChestCheckCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Player player = (Player) sender;

		Location loc = Objects.requireNonNull(player.getPlayer().getPlayer()).getLocation();

		Block block = loc.getBlock();
		loc = LocationSetComponent.normalise(loc);

		if (block.getType() != Material.CHEST) {
			return new CommandResponse("chest.claim.noChest");
		}

		Team claimedBy = Team.getClaimingTeam(block);

		if (claimedBy == null) {
			return new CommandResponse(true, "chest.check.notClaimed");
		}

		if (claimedBy == Team.getTeam(player)) {
			return new CommandResponse(true, "chest.check.self");
		}
		return new CommandResponse(true, new ReferencedFormatMessage("chest.check.claimed", claimedBy.getName()));

	}

	@Override
	public String getCommand() {
		return "check";
	}

	@Override
	public String getNode() {
		return "chest.check";
	}

	@Override
	public boolean needPlayer() {
		return true;
	}

	@Override
	public String getHelp() {
		return "Check if the chest you are standing on is claimed";
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
	protected boolean runAsync(String[] args) {
		return false;
	}
}
