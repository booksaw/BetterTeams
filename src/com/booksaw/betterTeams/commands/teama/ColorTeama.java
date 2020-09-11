package com.booksaw.betterTeams.commands.teama;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;

public class ColorTeama extends TeamSelectSubCommand {

	List<Character> banned = Arrays.asList(new Character[] { 'l', 'n', 'o', 'k', 'n', 'r' });

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {

		ChatColor color = null;
		try {
			color = ChatColor.valueOf(args[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			// expected if they do not input a correct value, or a char
		}
		if (color == null) {
			color = ChatColor.getByChar(args[1]);
			if (color == null || args[1].length() > 1)
				return new CommandResponse("color.fail");
		}

		if (banned.contains(color.getChar())) {
			return new CommandResponse("color.banned");
		}

		team.setColor(color);

		return new CommandResponse(true, "admin.color.success");
	}

	@Override
	public String getCommand() {
		return "color";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public String getNode() {
		return "admin.color";
	}

	@Override
	public String getHelp() {
		return "Change that teams color";
	}

	@Override
	public String getArguments() {
		return "<team> <color code>";
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 2) {
			for (ChatColor c : ChatColor.values()) {
				if (!banned.contains(c.getChar()) && c.name().toLowerCase().startsWith(args[1].toLowerCase())) {
					options.add(c.name().toLowerCase() + "");
				}
			}
		} else if (args.length == 1) {
			addTeamStringList(options, args[0]);
		}
	}

}
