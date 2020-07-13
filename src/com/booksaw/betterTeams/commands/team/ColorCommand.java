package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

public class ColorCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		if (teamPlayer.getRank() != PlayerRank.OWNER) {
			return new CommandResponse("needOwner");
		}

		ChatColor color = null;
		try {
			color = ChatColor.valueOf(args[0]);
		} catch (IllegalArgumentException e) {
			// expected if they do not input a correct value, or a char
		}
		if (color == null) {
			color = ChatColor.getByChar(args[0]);
			if (color == null || args[0].length() > 1)
				return new CommandResponse("color.fail");
		}
		System.out.println(color.name());
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
		for (ChatColor c : ChatColor.values()) {
			options.add(c.name() + "");
		}
	}

}
