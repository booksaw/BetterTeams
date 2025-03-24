package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.events.InventoryManagement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

public class EchestCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		InventoryManagement.adminViewers.put(player.getPlayer().getPlayer(), team);
		if (team.getEchest() == null || team.getEchest().getSize() == 0) {
			Main.plugin.getLogger().warning("EnderChest was found to be null or empty " + team.getEchest()
					+ " this should never occur, report to booksaw");
		}

		Bukkit.getScheduler().runTask(Main.plugin,
				() -> Objects.requireNonNull(player.getPlayer().getPlayer()).openInventory(team.getEchest()));

		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "echest";
	}

	@Override
	public String getNode() {
		return "echest";
	}

	@Override
	public String getHelp() {
		return "View your teams ender chest";
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
		return PlayerRank.DEFAULT;
	}

}
