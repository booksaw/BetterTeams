package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.HelpMessage;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class handles the command /team info [team/player]
 *
 * @author booksaw
 */
public class InfoCommand extends SubCommand {

	private final ParentCommand parentCommand;

	public InfoCommand(ParentCommand parentCommand) {
		this.parentCommand = parentCommand;
	}

	public static List<String> getInfoMessages(Team team) {
		List<String> infoMessages = new ArrayList<>();

		infoMessages.add(MessageManager.getMessage("info.name", team.getDisplayName()));
		if (team.getDescription() != null && !team.getDescription().isEmpty()) {
			infoMessages.add(MessageManager.getMessage("info.description", team.getDescription()));
		}

		infoMessages.add(MessageManager.getMessage("info.open", team.isOpen() + ""));
		infoMessages.add(MessageManager.getMessage("info.score", team.getScore() + ""));
		infoMessages.add(MessageManager.getMessage("info.money", team.getBalance()));
		infoMessages.add(MessageManager.getMessage("info.level", team.getLevel() + ""));
		infoMessages.add(MessageManager.getMessage("info.tag", team.getTag() + ""));

		String allyMessage = getAlliesMessage(team);
		if (allyMessage != null) {
			infoMessages.add(allyMessage);
		}

		String ownerPlayers = getPlayerList(team, PlayerRank.OWNER);
		if (ownerPlayers != null) {
			infoMessages.add(ownerPlayers);
		}

		String adminPlayers = getPlayerList(team, PlayerRank.ADMIN);
		if (adminPlayers != null) {
			infoMessages.add(adminPlayers);
		}

		String defaultPlayers = getPlayerList(team, PlayerRank.DEFAULT);
		if (defaultPlayers != null) {
			infoMessages.add(defaultPlayers);
		}

		return infoMessages;
	}

	private static String getAlliesMessage(Team team) {
		StringBuilder allies = new StringBuilder();
		for (UUID uuid : team.getAllies().getClone()) {
			Team ally = Team.getTeam(uuid);

			if (ally == null) {
				Bukkit.getLogger().warning("Unable to locate team with UUID: " + uuid);
				continue;
			}

			allies.append(ally.getDisplayName()).append(ChatColor.WHITE).append(", ");
		}
		if (allies.length() > 2) {
			allies = new StringBuilder(allies.substring(0, allies.length() - 2));

			return MessageManager.getMessage("info.ally", allies.toString());
		}
		return null;
	}

	private static String getPlayerList(Team team, PlayerRank rank) {
		List<TeamPlayer> users = team.getRank(rank);

		if (!users.isEmpty()) {
			StringBuilder userStr = new StringBuilder();
			ChatColor returnTo = ChatColor.RESET;
			String toTest = MessageManager.getMessage("info." + rank.toString().toLowerCase());
			if (toTest.length() > 1) {
				for (int i = toTest.length() - 1; i >= 0; i--) {
					if (toTest.charAt(i) == '\u00A7') {
						returnTo = ChatColor.getByChar(toTest.charAt(i + 1));
						break;
					}
				}
			}
			for (TeamPlayer player : users) {
				userStr.append(
						MessageManager.getMessage("info." + ((player.getPlayer().isOnline() && player.getOnlinePlayer().map(p -> !Utils.isVanished(p)).orElse(false)) ? "online" : "offline"))
								+ player.getPrefix(returnTo))
						.append(player.getPlayer().getName()).append(" ");
			}

			return MessageManager.getMessage("info." + rank.toString().toLowerCase(), userStr.toString());
		}

		return null;
	}

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				return new CommandResponse(new HelpMessage(this, label, parentCommand));
			}
			Team team = Team.getTeam((Player) sender);
			if (team == null) {
				return new CommandResponse(new HelpMessage(this, label, parentCommand));
			}
			displayTeamInfo(sender, team);
			return new CommandResponse(true);
		}

		// player or team has been entered
		// trying by team name
		Team team = Team.getTeam(args[0]);

		if (team != null) {
			displayTeamInfo(sender, team);
			return new CommandResponse(true);
		}

		// trying by player name
		/*
		 * method is depreciated as it does not guarantee the expected player, in most
		 * use cases this will work and it will be down to the user if it does not due
		 * to name changes This method is appropriate to use in this use case (so users
		 * can view offline users teams by name not just by team name)
		 */
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

		team = Team.getTeam(player);
		if (team != null) {
			displayTeamInfo(sender, team);
			return null;
		}
		return new CommandResponse("info.needTeam");

	}

	private void displayTeamInfo(CommandSender sender, Team team) {
		List<String> toDisplay = getInfoMessages(team);

		for (String str : toDisplay) {
			if (str.isEmpty()) {
				continue;
			}
			MessageManager.sendFullMessage(sender, str);
		}
	}

	@Override
	public String getCommand() {
		return "info";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "info";
	}

	@Override
	public String getHelp() {
		return "View information about the specified player / team";
	}

	@Override
	public String getArguments() {
		return "[team/player]";
	}

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

		addTeamStringList(options, (args.length == 0) ? "" : args[0]);
		addPlayerStringList(options, (args.length == 0) ? "" : args[0]);

	}

}
