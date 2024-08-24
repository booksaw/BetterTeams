package com.booksaw.betterTeams.integrations;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.DisbandTeamEvent;
import com.booksaw.betterTeams.customEvents.PlayerJoinTeamEvent;
import com.booksaw.betterTeams.customEvents.PlayerLeaveTeamEvent;
import com.booksaw.betterTeams.message.MessageManager;
import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.api.events.*;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.claim.ClaimDeleteReason;
import com.craftaro.ultimateclaims.member.ClaimMember;
import com.craftaro.ultimateclaims.member.ClaimRole;
import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public class UltimateClaimsManager implements Listener {

	public UltimateClaimsManager() {
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		Bukkit.getLogger().info("Registered UltimateClaims integration");
	}

	@EventHandler(ignoreCancelled = true)
	public void create(ClaimCreateEvent e) {
		OfflinePlayer p = e.getClaim().getOwner().getPlayer();
		Team team = Team.getTeam(p);

		if (team == null) {
			if (p.isOnline()) {
				MessageManager.sendMessage(p.getPlayer(), "uclaim.team");
			}
			e.setCancelled(true);
			return;
		}

		if (Objects.requireNonNull(team.getTeamPlayer(p)).getRank() != PlayerRank.OWNER) {
			if (p.isOnline()) {
				MessageManager.sendMessage(p.getPlayer(), "needOwner");
			}
			e.setCancelled(true);
		}
		e.getClaim().setName(team.getName());

		// they are owner of their team, adding the rest of the team members as members
		// of the claim
		for (TeamPlayer tp : team.getMembers().getClone()) {
			if (tp.getPlayer() != p) {
				ClaimMember member = e.getClaim().addMember(tp.getPlayer(), ClaimRole.MEMBER);
				JavaPlugin.getPlugin(UltimateClaims.class).getDataHelper().createMember(member);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onJoin(PlayerJoinTeamEvent e) {
		List<TeamPlayer> players = e.getTeam().getRank(PlayerRank.OWNER);

		for (TeamPlayer player : players) {
			Claim c = JavaPlugin.getPlugin(UltimateClaims.class).getClaimManager().getClaim(player.getPlayer().getUniqueId());
			if (c == null) {
				continue;
			}

			new WrappedRunnable() {

				@Override
				public void run() {
					ClaimMember member = c.addMember(e.getPlayer(), ClaimRole.MEMBER);
					JavaPlugin.getPlugin(UltimateClaims.class).getDataHelper().createMember(member);
				}
			}.runTaskLater(Main.plugin.getScheduler(), 1);

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onLeave(PlayerLeaveTeamEvent e) {
		List<TeamPlayer> players = e.getTeam().getRank(PlayerRank.OWNER);

		for (TeamPlayer player : players) {
			Claim c = JavaPlugin.getPlugin(UltimateClaims.class).getClaimManager().getClaim(player.getPlayer().getUniqueId());
			if (c == null) {
				continue;
			}

			ClaimMember member = c.getMember(e.getPlayer());
			c.removeMember(member);
			JavaPlugin.getPlugin(UltimateClaims.class).getDataHelper().deleteMember(member);

		}
	}

	@EventHandler(ignoreCancelled = true)
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

	@EventHandler(ignoreCancelled = true)
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

	@EventHandler(ignoreCancelled = true)
	public void transferEvent(ClaimTransferOwnershipEvent e) {
		Team team = Team.getTeam(e.getNewOwner());
		if (team == null || Objects.requireNonNull(team.getTeamPlayer(e.getNewOwner())).getRank() != PlayerRank.OWNER) {
			Bukkit.getLogger().info("");
			Bukkit.getLogger().info("");
			Bukkit.getLogger().info("You cannot transfer ownership to a player who is not an owner of a team");
			Bukkit.getLogger().info("");
			Bukkit.getLogger().info("");
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void disbandEvent(DisbandTeamEvent event) {

		for (TeamPlayer player : event.getTeam().getRank(PlayerRank.OWNER)) {
			Claim c = JavaPlugin.getPlugin(UltimateClaims.class).getClaimManager().getClaim(player.getPlayer().getUniqueId());
			if (c == null) {
				continue;
			}

			new WrappedRunnable() {
				
				@Override
				public void run() {
					c.destroy(ClaimDeleteReason.PLAYER);
				}
			}.runTask(Main.plugin.getScheduler());
			

			if (player.getPlayer().isOnline()) {
				MessageManager.sendMessage(player.getPlayer().getPlayer(), "uclaim.dissolve");
			}
		}

	}

}
