package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;

public class TagCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (args.length == 0) {
			return new CommandResponse(new ReferencedFormatMessage("info.tag", team.getTag()));
		}

		if (player.getRank() != PlayerRank.OWNER) {
			MessageManager.sendMessageF(player.getPlayer().getPlayer(), "info.tag", team.getTag());
			return new CommandResponse("needOwner");
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

}
