package com.booksaw.betterTeams.commands.team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class ColorCommand extends TeamSubCommand {

	final List<Character> alwaysBanned = Arrays.asList('l', 'n', 'o', 'k', 'n', 'r');
	private List<Character> banned;

	public ColorCommand() {
		banned = new ArrayList<>();
		banned.addAll(alwaysBanned);
		banned.addAll(Main.plugin.getConfig().getString("bannedColors").chars().mapToObj(c -> (char) c)
				.collect(Collectors.toList()));
	}

	@Override
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		ChatColor color = null;
		try {
			color = ChatColor.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			// expected if they do not input a correct value, or a char
		}
		if (color == null) {
			color = ChatColor.getByChar(args[0]);
			if (color == null || args[0].length() > 1)
				return new CommandResponse("color.fail");
		}

		if (banned.contains(color.getChar())) {
			return new CommandResponse("color.banned");
		}

		team.setColor(color);

		return new CommandResponse(true, "color.success");
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
		return "color";
	}

	@Override
	public String getHelp() {
		return "Change your teams color";
	}

	@Override
	public String getArguments() {
		return "<color code>";
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			for (ChatColor c : ChatColor.values()) {
				if (!banned.contains(c.getChar()) && c.name().toLowerCase().startsWith(args[0].toLowerCase())) {
					options.add(c.name().toLowerCase() + "");
				}
			}
		}
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
