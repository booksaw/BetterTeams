package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		Team team = Team.getTeam(args[0]);
		if (team == null) {
			return new CommandResponse("admin.noTeam");
		}

		if (team.getTeamHome() == null) {
			return new CommandResponse("admin.home.noHome");
		}

		Player p = (Player) sender;
		Bukkit.getScheduler().runTask(Main.plugin, () -> p.teleport(team.getTeamHome()));

		return new CommandResponse(true, "admin.home.success");
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

		addTeamStringList(options, args[0]);

	}

}
