package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.commands.HelpCommand;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.SubCommand;
import net.md_5.bungee.api.ChatColor;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class HelpMessage implements Message {

	final SubCommand command;
	final ParentCommand parent;
	final String label;

	public HelpMessage(SubCommand command, String label, ParentCommand parent) {
		this.command = command;
		this.label = label;
		this.parent = parent;
	}

	@Override
	public void sendMessage(CommandSender sender) {
		MessageManager.sendFullMessage(sender, createHelpMessage(label,
				command.getCommand() + " " + command.getArgMessage(parent), command.getHelpMessage(parent)));
	}

	@Override
	public void sendTitle(Player player) {
		MessageManager.sendFullTitle(player, createHelpMessage(label,
				command.getCommand() + " " + command.getArgMessage(parent), command.getHelpMessage(parent)));
	}

	@Override
	public void sendMessage(Collection<? extends CommandSender> senders) {
		MessageManager.sendFullMessage(senders, createHelpMessage(label,
				command.getCommand() + " " + command.getArgMessage(parent), command.getHelpMessage(parent)));
	}

	@Override
	public void sendMessage(Collection<? extends CommandSender> senders, Player player) {
		sendMessage(senders);
	}

	@Override
	public void sendTitle(Collection<? extends Player> players) {
		MessageManager.sendFullTitle(players, createHelpMessage(label,
				command.getCommand() + " " + command.getArgMessage(parent), command.getHelpMessage(parent)));
	}

	@Override
	public void sendTitle(Collection<? extends Player> players, Player player) {
		sendTitle(players);
	}

	/**
	 * Used to create a formatted help message to explain what a command does to the
	 * user
	 *
	 * @param label       the base command
	 * @param commandPath the rest of the command (i.e. help [param])
	 * @param description the description of the command
	 * @return the created message relating to that command
	 */
	public String createHelpMessage(String label, String commandPath, String description) {
		return HelpCommand.prefix + "/" + label + " " + commandPath + ChatColor.WHITE + " - " + HelpCommand.description
				+ description;
	}

}
