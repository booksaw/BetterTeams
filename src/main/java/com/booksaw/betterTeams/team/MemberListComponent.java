package com.booksaw.betterTeams.team;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.PlayerJoinTeamEvent;
import com.booksaw.betterTeams.customEvents.PlayerLeaveTeamEvent;
import com.booksaw.betterTeams.exceptions.CancelledEventException;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class MemberListComponent extends TeamPlayerListComponent {

	@Override
	public void add(Team team, TeamPlayer teamPlayer) {

		// calling the event
		PlayerJoinTeamEvent event = new PlayerJoinTeamEvent(team, teamPlayer);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			throw new CancelledEventException(event);
		}

		OfflinePlayer p = teamPlayer.getPlayer();

		team.getInvitedPlayers().remove(p.getUniqueId());

		// if the player is offline there will be no player object for them
		if (p.isOnline()) {
			for (TeamPlayer player : list) {
				if (player.getPlayer().isOnline()) {
					MessageManager.sendMessageF((CommandSender) player.getPlayer().getPlayer(), "join.notify",
							p.getPlayer().getDisplayName());
				}
			}

			if (Main.plugin.teamManagement != null) {
				Main.plugin.teamManagement.displayBelowName(p.getPlayer());
			}
		}

		Team.getTeamManager().playerJoinTeam(team, teamPlayer);
		list.add(teamPlayer);

	}

	@Override
	public void remove(Team team, TeamPlayer teamPlayer) {

		PlayerLeaveTeamEvent event = new PlayerLeaveTeamEvent(team, teamPlayer);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			throw new CancelledEventException(event);
		}

		OfflinePlayer p = teamPlayer.getPlayer();

		if (Main.plugin.teamManagement != null && p.isOnline()) {
			Main.plugin.teamManagement.remove(p.getPlayer());
		}
		Team.getTeamManager().playerLeaveTeam(team, teamPlayer);
		list.remove(teamPlayer);
	}

	@Override
	public String getSectionHeading() {
		return "players";
	}

	@Override
	public void load(TeamStorage section) {
		load(section.getPlayerList());
	}

	@Override
	public void save(TeamStorage storage) {
		storage.setPlayerList(getConvertedList());
	}

}
