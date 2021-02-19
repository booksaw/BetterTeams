package com.booksaw.betterTeams.commands.team;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.HelpMessage;
import com.booksaw.betterTeams.message.MessageManager;

/**
 * This class handles the command /team info [team/player]
 * 
 * @author booksaw
 *
 */
public class InfoCommand extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				return new CommandResponse(new HelpMessage(this, label));
			}
			Team team = Team.getTeam((Player) sender);
			if (team == null) {
				return new CommandResponse(new HelpMessage(this, label));
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

		if (player != null) {
			team = Team.getTeam(player);
			if (team != null) {
				displayTeamInfo(sender, team);
				return null;
			}
			return new CommandResponse("info.needTeam");
		}

		return new CommandResponse("info.fail");
	}

	private void displayTeamInfo(CommandSender sender, Team team) {
		MessageManager.sendMessageF(sender, "info.name", team.getDisplayName());
		if (team.getDescription() != null && !team.getDescription().equals("")) {
			MessageManager.sendMessageF(sender, "info.description", team.getDescription());
		}

		MessageManager.sendMessageF(sender, "info.open", team.isOpen() + "");
		MessageManager.sendMessageF(sender, "info.score", team.getScore() + "");
		MessageManager.sendMessageF(sender, "info.money", team.getBalance());
		MessageManager.sendMessageF(sender, "info.level", team.getLevel() + "");
		MessageManager.sendMessageF(sender, "info.tag", team.getTag() + "");

		String allies = "";
		for (UUID uuid : team.getAllies()) {
			allies = allies + Team.getTeam(uuid).getDisplayName() + ChatColor.WHITE + ", ";
		}
		if (allies.length() > 2) {
			allies = allies.substring(0, allies.length() - 2);

			MessageManager.sendMessageF(sender, "info.ally", allies);
		}

		List<TeamPlayer> owners = team.getRank(PlayerRank.OWNER);

		if (owners.size() > 0) {
			String ownerStr = "";
			ChatColor returnTo = ChatColor.RESET;
			String toTest = MessageManager.getMessage("info.owner");
			if (toTest.length() > 1) {
				for (int i = toTest.length() - 1; i >= 0; i--) {
					if (toTest.charAt(i) == '§') {
						returnTo = ChatColor.getByChar(toTest.charAt(i + 1));
						if (toTest != null) {
							break;
						}
					}
				}
			}

			for (TeamPlayer player : owners) {
				ownerStr = ownerStr + player.getPrefix(returnTo) + player.getPlayer().getName() + " ";
			}
			MessageManager.sendMessageF(sender, "info.owner", ownerStr);
		}

		List<TeamPlayer> admins = team.getRank(PlayerRank.ADMIN);

		if (admins.size() > 0) {
			String adminStr = "";
			ChatColor returnTo = ChatColor.RESET;
			String toTest = MessageManager.getMessage("info.admin");
			if (toTest.length() > 1) {
				for (int i = toTest.length() - 1; i >= 0; i--) {
					if (toTest.charAt(i) == '§') {
						returnTo = ChatColor.getByChar(toTest.charAt(i + 1));
						if (toTest != null) {
							break;
						}
					}
				}
			}
			for (TeamPlayer player : admins) {
				adminStr = adminStr + player.getPrefix(returnTo) + player.getPlayer().getName() + " ";
			}

			MessageManager.sendMessageF(sender, "info.admin", adminStr);
		}

		List<TeamPlayer> users = team.getRank(PlayerRank.DEFAULT);

		if (users.size() > 0) {
			String userStr = "";
			ChatColor returnTo = ChatColor.RESET;
			String toTest = MessageManager.getMessage("info.default");
			if (toTest.length() > 1) {
				for (int i = toTest.length() - 1; i >= 0; i--) {
					if (toTest.charAt(i) == '§') {
						returnTo = ChatColor.getByChar(toTest.charAt(i + 1));
						if (toTest != null) {
							break;
						}
					}
				}
			}
			for (TeamPlayer player : users) {
				userStr = userStr + player.getPrefix(returnTo) + player.getPlayer().getName() + " ";
			}

			MessageManager.sendMessageF(sender, "info.default", userStr);
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

		for (Entry<UUID, Team> team : Team.getTeamList().entrySet()) {
			if (team.getValue().getName().startsWith(args[0]))
				options.add(team.getValue().getName());
		}
		addPlayerStringList(options, (args.length == 0) ? "" : args[0]);

	}

}
