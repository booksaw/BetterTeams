package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TagCommand extends TeamSubCommand {

	public TagCommand() {
		checkRank = false;
	}

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (args.length == 0) {
			return new CommandResponse(new ReferencedFormatMessage("info.tag", team.getTag()));
		}

		if (player.getRank().value < getRequiredRank().value) {
			MessageManager.sendMessageF(player.getPlayer().getPlayer(), "info.tag", team.getTag());
			return new CommandResponse("tag.noPerm");
		}

		if (!Team.isValidTeamName(args[0])) {
			return new CommandResponse("tag.banned");
		}

		int max = Main.plugin.getConfig().getInt("maxTagLength");
		if (max > 55) {
			max = 55;
		}
		if (max != -1 && max < args[0].length() && !args[0].equals(team.getName())) {
			return new CommandResponse("tag.maxLength");
		}

		team.setTag(args[0]);

		return new CommandResponse(true, "tag.success");
	}

	@Override
	public String getCommand() {
		return "tag";
	}

	@Override
	public String getNode() {
		return "tag";
	}

	@Override
	public String getHelp() {
		return "Set your teams tag";
	}

	@Override
	public String getArguments() {
		return "[tag]";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			options.add("[tag]");
		}
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
