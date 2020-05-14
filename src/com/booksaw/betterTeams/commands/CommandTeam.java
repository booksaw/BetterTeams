package com.booksaw.betterTeams.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.commands.team.BanCommand;
import com.booksaw.betterTeams.commands.team.CreateCommand;
import com.booksaw.betterTeams.commands.team.DemoteCommand;
import com.booksaw.betterTeams.commands.team.DescriptionCommand;
import com.booksaw.betterTeams.commands.team.DisbandCommand;
import com.booksaw.betterTeams.commands.team.HelpCommand;
import com.booksaw.betterTeams.commands.team.HomeCommand;
import com.booksaw.betterTeams.commands.team.InfoCommand;
import com.booksaw.betterTeams.commands.team.InviteCommand;
import com.booksaw.betterTeams.commands.team.JoinCommand;
import com.booksaw.betterTeams.commands.team.KickCommand;
import com.booksaw.betterTeams.commands.team.LeaveCommand;
import com.booksaw.betterTeams.commands.team.NameCommand;
import com.booksaw.betterTeams.commands.team.OpenCommand;
import com.booksaw.betterTeams.commands.team.PromoteCommand;
import com.booksaw.betterTeams.commands.team.SethomeCommand;
import com.booksaw.betterTeams.commands.team.UnbanCommand;

/**
 * This is used to direct the team command to the subCommand
 */
public class CommandTeam implements CommandExecutor {

	private ParentCommand teamCommand;

	public CommandTeam() {
		teamCommand = new ParentCommand("team", new HelpCommand());
		// add all sub commands here
		teamCommand.addSubCommand(new CreateCommand());
		teamCommand.addSubCommand(new LeaveCommand());
		teamCommand.addSubCommand(new DisbandCommand());
		teamCommand.addSubCommand(new DescriptionCommand());
		teamCommand.addSubCommand(new InviteCommand());
		teamCommand.addSubCommand(new JoinCommand());
		teamCommand.addSubCommand(new NameCommand());
		teamCommand.addSubCommand(new OpenCommand());
		teamCommand.addSubCommand(new InfoCommand());
		teamCommand.addSubCommand(new KickCommand());
		teamCommand.addSubCommand(new PromoteCommand());
		teamCommand.addSubCommand(new DemoteCommand());
		teamCommand.addSubCommand(new HomeCommand());
		teamCommand.addSubCommand(new SethomeCommand());
		teamCommand.addSubCommand(new BanCommand());
		teamCommand.addSubCommand(new UnbanCommand());

	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		// running custom command manager
		teamCommand.onCommand(sender, label, args);
		return true;
	}

}
