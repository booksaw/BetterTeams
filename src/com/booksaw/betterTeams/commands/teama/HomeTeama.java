package com.booksaw.betterTeams.commands.teama;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;

public class HomeTeama extends SubCommand {

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		Team team = Team.getTeam(args[0]);
		if (team == null) {
			return "admin.noTeam";
		}

		if(team.getTeamHome() == null) {
			return "admin.home.noHome";
		}
		
		Player p = (Player)sender; 
		p.teleport(team.getTeamHome()); 
		
		return "admin.home.success";
	}

	@Override
	public String getCommand() {
		return "home";
	}

	@Override
	public String getNode() {
		return "admin.home";
	}

	@Override
	public String getHelp() {
		return "teleport to another teams home";
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
	public boolean needPlayer() {
		return true;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

		for (Entry<UUID, Team> team : Team.getTeamList().entrySet()) {
			options.add(team.getValue().getName());
		}

	}

}
