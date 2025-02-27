/**
 * 
 */
package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class NeutralTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		
		Team team1 = Team.getTeam(args[0]);
		Team team2 = Team.getTeam(args[1]);
		
		if(team1 == null || team2 == null) {
			return new CommandResponse("admin.noTeam");
		}
		
		if(team1 == team2) {
			return new CommandResponse("admin.neutral.same");
		}
		
		if(!team1.isAlly(team2)) {
			return new CommandResponse("admin.neutral.not");
		}
		
		team1.removeAlly(team2);
		team2.removeAlly(team1);
		
		return new CommandResponse(true, "admin.neutral.success");
	}

	@Override
	public String getCommand() {
		return "neutral";
	}

	@Override
	public String getNode() {
		return "admin.neutral";
	}

	@Override
	public String getHelp() {
		return "Set two teams to no longer be allies";
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
