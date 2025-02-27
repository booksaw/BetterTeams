package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DisbandTeama extends TeamSelectSubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {
		if (sender instanceof Player) {
			team.disband((Player)sender);
		} else {
			team.disband();
		}

		return new CommandResponse(true, "admin.disband.success");
	}

	@Override
	public String getCommand() {
		return "disband";
	}

	@Override
	public String getNode() {
		return "admin.disband";
	}

	@Override
	public String getHelp() {
		return "Disband the specified team" + ChatColor.RED + " THIS CANNOT BE UNDONE!!!!!";
	}

	@Override
	public String getArguments() {
		return "<team>";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		}

	}

}
