package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.team.level.LevelManager;
import com.booksaw.betterTeams.team.level.TeamLevel;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class RankCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Team team = null;

		if (args.length == 0 && sender instanceof Player) {
			team = Team.getTeam((Player) sender);
		} else if (args.length >= 1) {
			team = Team.getTeam(args[0]);
		}

		if (team == null) {
			return new CommandResponse("rank.noTeam");
		}

		TeamLevel currentLevel = team.getLevelObject();
		TeamLevel nextLevel = LevelManager.getNextLevel(team.getLevel());

		if (nextLevel == null) {
			new ReferencedFormatMessage("rank.infomm", team.getLevel()).sendMessage(sender);
		} else {
			boolean score = nextLevel.isScoreCost();
			String costString = String.valueOf((int) nextLevel.getCostValue());

			new ReferencedFormatMessage("rank.info" + ((score) ? "s" : "m"), team.getLevel(),
					costString).sendMessage(sender);

		}

		List<String> rankLore = currentLevel.getColoredLore();

		if (rankLore.isEmpty()) {
			return new CommandResponse(true);
		}

		for (String s : rankLore) {
			MessageManager.sendFullMessage(sender, s);
		}

		return new CommandResponse(true);
	}

	@Override
	public String getCommand() {
		return "rank";
	}

	@Override
	public String getNode() {
		return "rank";
	}

	@Override
	public String getHelp() {
		return "View the rank of a team";
	}

	@Override
	public String getArguments() {
		return "[team]";
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
		addTeamStringList(options, args[0]);

	}

}
