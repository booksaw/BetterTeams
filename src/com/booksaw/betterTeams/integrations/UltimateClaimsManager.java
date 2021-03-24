package com.booksaw.betterTeams.integrations;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.PlayerJoinTeamEvent;
import com.booksaw.betterTeams.customEvents.PlayerLeaveTeamEvent;
import com.booksaw.betterTeams.message.MessageManager;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.api.events.ClaimCreateEvent;
import com.songoda.ultimateclaims.api.events.ClaimMemberLeaveEvent;
import com.songoda.ultimateclaims.api.events.ClaimPlayerBanEvent;
import com.songoda.ultimateclaims.api.events.ClaimPlayerKickEvent;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;

public class UltimateClaimsManager implements Listener {

	/*
	 * You can listen to ClaimCreateEvent, check who is the owner, and add the
	 * member with Claim#addMember. For this to persist, you need to save it to the
	 * database, which can be achieved with
	 * UltimateClaims.getInstance().getDataManager().createMember(ClaimMember).
	 * Claim#addMember returns the ClaimMember object you need.
	 */

	/*
	 * BUGS: if I delete a powercell in a claim there is no message saying when the
	 * claim will expire
	 */
	/*
	 * TODO; remove player on team leave, add players on join, name claims, config
	 * option to disable this manager
	 */
	public UltimateClaimsManager() {
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		System.out.println("registered");
	}

	// used to check that the event hander is registering events correctly
//	@EventHandler
//	public void onMove(PlayerMoveEvent e) {
//		System.out.println("called move");
//	}

	@EventHandler
	public void create(ClaimCreateEvent e) {
		OfflinePlayer p = e.getClaim().getOwner().getPlayer();
		Team team = Team.getTeam(p);

		if (team == null) {
			if (p.isOnline()) {
				MessageManager.sendMessage(p.getPlayer(), "uclaim.team");
			}
			e.setCancelled(true);
		}

		if (team.getTeamPlayer(p).getRank() != PlayerRank.OWNER) {
			if (p.isOnline()) {
				MessageManager.sendMessage(p.getPlayer(), "needOwner");
			}
			e.setCancelled(true);
		}

		// they are owner of their team, adding the rest of the team members as members
		// of the claim
		for (TeamPlayer tp : team.getMembers()) {
			if (tp.getPlayer() != p) {
				ClaimMember member = e.getClaim().addMember(tp.getPlayer(), ClaimRole.MEMBER);
				UltimateClaims.getInstance().getDataManager().createMember(member);

				System.out.println("SAVING MEMBER " + member.getName());
			}
		}

	}

	@EventHandler
	public void onJoin(PlayerJoinTeamEvent e) {
		List<TeamPlayer> players = e.getTeam().getRank(PlayerRank.OWNER);

		for (TeamPlayer player : players) {
			System.out.println("checking player " + player.getPlayer().getName());
			Claim c = UltimateClaims.getInstance().getClaimManager().getClaim(player.getPlayer().getUniqueId());
			if (c == null) {
				continue;
			}

			System.out.println("claim found for plkayer, adding new player");
			ClaimMember member = c.addMember(e.getPlayer(), ClaimRole.MEMBER);
			UltimateClaims.getInstance().getDataManager().createMember(member);

		}
	}

	@EventHandler
	public void onLeave(PlayerLeaveTeamEvent e) {
		List<TeamPlayer> players = e.getTeam().getRank(PlayerRank.OWNER);

		for (TeamPlayer player : players) {
			System.out.println("Leave checking player " + player.getPlayer().getName());
			Claim c = UltimateClaims.getInstance().getClaimManager().getClaim(player.getPlayer().getUniqueId());
			if (c == null) {
				continue;
			}
			System.out.println("claim found, removing player");

			ClaimMember member = c.getMember(e.getPlayer());
			c.removeMember(member);
			UltimateClaims.getInstance().getDataManager().deleteMember(member);

		}
	}

	@EventHandler
	public void kickEvent(ClaimPlayerKickEvent e) {
		Team team = Team.getTeam(e.getClaim().getOwner().getPlayer());
		Team team2 = Team.getTeam(e.getPlayer());
		if (team == team2) {
			e.setCancelled(true);
			if (e.getClaim().getOwner().getPlayer().isOnline()) {
				MessageManager.sendMessage(e.getClaim().getOwner().getPlayer().getPlayer(), "uclaim.kick");
			}
		}
	}

	@EventHandler
	public void banEvent(ClaimPlayerBanEvent e) {
		Team team = Team.getTeam(e.getClaim().getOwner().getPlayer());
		Team team2 = Team.getTeam(e.getBannedPlayer());
		if (team == team2) {
			e.setCancelled(true);
			if (e.getClaim().getOwner().getPlayer().isOnline()) {
				MessageManager.sendMessage(e.getClaim().getOwner().getPlayer().getPlayer(), "uclaim.ban");
			}
		}
	}

	@EventHandler
	public void leaveEvent(ClaimMemberLeaveEvent e) {
		Team team = Team.getTeam(e.getClaim().getOwner().getPlayer());
		Team team2 = Team.getTeam(e.getPlayer());
		if (team == team2) {
			e.setCancelled(true);
			if (e.getPlayer().isOnline()) {
				MessageManager.sendMessage(e.getPlayer().getPlayer(), "uclaim.member");
			}
		}
	}

}
