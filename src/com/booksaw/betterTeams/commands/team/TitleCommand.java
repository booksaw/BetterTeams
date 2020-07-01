package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;

public class TitleCommand extends TeamSubCommand {

	private final char[] bannedChars = new char[] { ',', '§' };
	private final String[] bannedColor = new String[] { "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a",
			"&b", "&c", "&d", "&e", "&f" };

	Team team;

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		TeamPlayer toTitle = null;

		if (args[0].equals("me")) {
			toTitle = player;
		} else {
			toTitle = team.getTeamPlayer(Bukkit.getPlayer(args[0]));
		}

		if (toTitle == null) {
			return new CommandResponse("noPlayer");
		}

		if (player.getRank() != PlayerRank.OWNER
				&& !(player == toTitle && player.getPlayer().getPlayer().hasPermission("betterteams.title.self"))) {
			return new CommandResponse("needOwner");
		}

		if (args.length == 1) {
			team.setTitle(toTitle, "");
			MessageManager.sendMessage(toTitle.getPlayer().getPlayer(), "title.reset");
			return new CommandResponse("title.success");
		}

		if (args[1].length() > Main.plugin.getConfig().getInt("maxTitleLength")) {
			return new CommandResponse("title.tooLong");
		}

		for (char bannedChar : bannedChars) {
			if (args[1].contains(bannedChar + "")) {
				return new CommandResponse("bannedChar");
			}
		}

		Player sender = player.getPlayer().getPlayer();

		if (!sender.hasPermission("betterteams.title.color.format")) {
			if (args[1].contains("&l") || args[1].contains("&k") || args[1].contains("&n") || args[1].contains("&m")
					|| args[1].contains("&o")) {
				return new CommandResponse("title.noFormat");
			}
		}

		if (!sender.hasPermission("betterteams.title.color.color")) {
			for (String bannedChar : bannedColor) {
				if (args[1].contains(bannedChar)) {
					return new CommandResponse("title.noColor");
				}
			}
		}

		if (sender.hasPermission("betterteams.title.color.color")
				|| sender.hasPermission("betterteams.title.color.format")) {
			args[1] = ChatColor.translateAlternateColorCodes('&', args[1]);
		}

		team.setTitle(toTitle, args[1]);

		if (player != toTitle && toTitle.getPlayer().isOnline()) {
			MessageManager.sendMessageF(toTitle.getPlayer().getPlayer(), "title.change", args[1]);
		}

		return new CommandResponse(true, "title.success");
	}

	@Override
	public String getCommand() {
		return "title";
	}

	@Override
	public String getNode() {
		return "title";
	}

	@Override
	public String getHelp() {
		return "Change that players title within the team";
	}

	@Override
	public String getArguments() {
		return "<player/me> <title>";
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

		if (args.length == 0) {
			return;
		}

		if (args.length < 2) {

			addPlayerStringList(options, args[0]);
			return;
		}

		options.add("<title>");
	}
}
