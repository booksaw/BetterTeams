/**
 * 
 */
package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AllyTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		
		Team team1 = Team.getTeam(args[0]);
		Team team2 = Team.getTeam(args[1]);
		
		if(team1 == null || team2 == null) {
			return new CommandResponse("admin.noTeam");
		}
		
		if(team1 == team2) {
			return new CommandResponse("admin.ally.same");
		}
		
		if(team1.isAlly(team2.getID())) {
			return new CommandResponse("admin.ally.already");
		}
		
		team1.addAlly(team2.getID());
		team2.addAlly(team1.getID());
		team1.removeAllyRequest(team2.getID());
		team2.removeAllyRequest(team1.getID());
		
		return new CommandResponse(true, "admin.ally.success");
	}

	@Override
	public String getCommand() {
		return "ally";
	}

	@Override
	public String getNode() {
		return "admin.ally";
	}

	@Override
	public String getHelp() {
		return "Set two teams to be allies of each other";
	}

	@Override
	public String getArguments() {
		return "<team1> <team2>";
	}

	@Override
	public int getMinimumArguments() {
		return 2;
	}

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		}
		if (args.length == 2) {
			addTeamStringList(options, args[1]);
		}
	}



}
