package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class TitleCommand extends TeamSubCommand {

	private final String[] bannedColor = new String[] { "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a",
			"&b", "&c", "&d", "&e", "&f" };

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		TeamPlayer toTitle;

		if (args.length == 0) {
			args = new String[] { "me", null };
		}

		if (args.length == 1) {
			args = new String[] { "me", args[0] };
		}

		Player toTitlePlayer;

		if (args[0].equals("me")) {
			// player is online so this should not cause any issues
			toTitlePlayer = player.getPlayer().getPlayer();
		} else {
			toTitlePlayer = Bukkit.getPlayer(args[0]);
		}

		if (toTitlePlayer == null) {
			return new CommandResponse("noPlayer");
		}

		toTitle = team.getTeamPlayer(toTitlePlayer);

		if (toTitle == null) {
			return new CommandResponse("noPlayer");
		}

		if (player.getRank().value < getRequiredRank().value && !(player == toTitle
				&& Objects.requireNonNull(player.getPlayer().getPlayer()).hasPermission("betterteams.title.self"))) {
			return new CommandResponse("title.noPerm");
		}
		
		if (args[1] == null) {
			team.setTitle(toTitle, "");
			return new CommandResponse(true, "title.remove");
		}

		if (args[1] != null && args[1].length() > Main.plugin.getConfig().getInt("maxTitleLength")) {
			return new CommandResponse("title.tooLong");
		}

		if (!Team.isValidTeamName(args[1])) {
			return new CommandResponse("bannedChar");
		}

		Player sender = player.getPlayer().getPlayer();

		if (!Objects.requireNonNull(sender).hasPermission("betterteams.title.color.format")) {
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
			if (args[1] != null) {
				MessageManager.sendMessage(toTitle.getPlayer().getPlayer(), "title.change", args[1]);
			} else {
				MessageManager.sendMessage(toTitle.getPlayer().getPlayer(), "title.remove");
			}
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
		return "Change that players title or remove it within the team";
	}

	@Override
	public String getArguments() {
		return "[player/me] [title]";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
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

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}
}
