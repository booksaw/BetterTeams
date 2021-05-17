package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TitleTeama extends SubCommand {

	private final char[] bannedChars = new char[] { ',', '§' };

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Team team = Team.getTeam(Bukkit.getPlayer(args[0]));

		if (team == null) {
			return new CommandResponse("admin.inTeam");
		}

		TeamPlayer toTitle = team.getTeamPlayer(Bukkit.getPlayer(args[0]));

		if (toTitle == null) {
			return new CommandResponse("noPlayer");
		}

		if (args.length == 1) {
			team.setTitle(toTitle, "");
			MessageManager.sendMessage(toTitle.getPlayer().getPlayer(), "admin.title.reset");
			return new CommandResponse(true, "title.success");
		}

		if (args[1].length() > Main.plugin.getConfig().getInt("maxTitleLength")) {
			return new CommandResponse("title.tooLong");
		}

		for (char bannedChar : bannedChars) {
			if (args[1].contains(bannedChar + "")) {
				return new CommandResponse("bannedChar");
			}
		}

		args[1] = ChatColor.translateAlternateColorCodes('&', args[1]);

		team.setTitle(toTitle, args[1]);

		MessageManager.sendMessageF(toTitle.getPlayer().getPlayer(), "title.change", args[1]);

		return new CommandResponse(true, "admin.title.success");
	}

	@Override
	public String getCommand() {
		return "title";
	}

	@Override
	public String getNode() {
		return "admin.title";
	}

	@Override
	public String getHelp() {
		return "Change a players title to the specified title";
	}

	@Override
	public String getArguments() {
		return "<player> <title>";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addPlayerStringList(options, args[0]);
		}
		if (args.length == 2) {
			options.add("<title>");
		}
	}

}
