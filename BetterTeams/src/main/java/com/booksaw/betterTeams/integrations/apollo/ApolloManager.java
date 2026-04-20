package com.booksaw.betterTeams.integrations.apollo;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.post.PostDisbandTeamEvent;
import com.booksaw.betterTeams.customEvents.post.PostPlayerJoinTeamEvent;
import com.booksaw.betterTeams.customEvents.post.PostPlayerLeaveTeamEvent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Integrates BetterTeams with the Apollo (Lunar Client) Team View module
 * using the lightweight JSON plugin messaging API.
 * <p>
 * Detects players on Lunar Client via the {@code lunar:apollo} channel and
 * sends periodic team-member location updates over the {@code apollo:json}
 * channel so teammates appear as markers on the minimap and direction HUD.
 */
public class ApolloManager implements Listener {

	private static final String APOLLO_CHANNEL = "apollo:json";
	private static final String LUNAR_CHANNEL = "lunar:apollo";

	/** UUIDs of online players confirmed to be running Lunar Client with Apollo. */
	private static final Set<UUID> apolloPlayers = new HashSet<>();

	public ApolloManager() {
		var messenger = Bukkit.getServer().getMessenger();
		messenger.registerIncomingPluginChannel(Main.plugin, LUNAR_CHANNEL, (channel, player, bytes) -> {});
		messenger.registerIncomingPluginChannel(Main.plugin, APOLLO_CHANNEL, (channel, player, bytes) -> {});
		messenger.registerOutgoingPluginChannel(Main.plugin, APOLLO_CHANNEL);

		Bukkit.getPluginManager().registerEvents(this, Main.plugin);

		Main.plugin.getFoliaLib().getScheduler().runTimerAsync(this::refreshAllTeams, 1L, 1L);
		Main.plugin.getLogger().info("Registered Apollo Teamview integration");
	}

	@EventHandler
	public void onRegisterChannel(PlayerRegisterChannelEvent event) {
		if (!event.getChannel().equalsIgnoreCase(LUNAR_CHANNEL)) {
			return;
		}

		Player player = event.getPlayer();
		apolloPlayers.add(player.getUniqueId());

		// Instant refresh
		Team team = Team.getTeam(player);
		if (team != null) {
			refreshTeam(team);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		apolloPlayers.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoinTeam(PostPlayerJoinTeamEvent event) {
		refreshTeam(event.getTeam());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeaveTeam(PostPlayerLeaveTeamEvent event) {
		OfflinePlayer leaving = event.getPlayer();
		if (leaving.isOnline()) {
			Player player = leaving.getPlayer();
			if (apolloPlayers.contains(player.getUniqueId())) {
				sendResetTeamMembers(player);
			}
		}

		refreshTeam(event.getTeam());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDisbandTeam(PostDisbandTeamEvent event) {
		for (TeamPlayer tp : event.getPrevMembers()) {
			tp.getOnlinePlayer().ifPresent(player -> {
				if (apolloPlayers.contains(player.getUniqueId())) {
					sendResetTeamMembers(player);
				}
			});
		}
	}

	private void refreshAllTeams() {
		Team.getTeamManager().getLoadedTeamListClone().values().forEach(this::refreshTeam);
	}

	private void refreshTeam(Team team) {
		Set<TeamPlayer> members = team.getMembers().getClone();

		JsonArray teammates = new JsonArray();
		for (TeamPlayer tp : members) {
			tp.getOnlinePlayer().ifPresent(player ->
				teammates.add(createTeamMemberObject(player, team))
			);
		}

		if (teammates.isEmpty()) {
			return;
		}

		JsonObject message = new JsonObject();
		message.addProperty("@type", "type.googleapis.com/lunarclient.apollo.team.v1.UpdateTeamMembersMessage");
		message.add("members", teammates);

		for (TeamPlayer tp : members) {
			tp.getOnlinePlayer().ifPresent(player -> {
				if (apolloPlayers.contains(player.getUniqueId())) {
					sendPacket(player, message);
				}
			});
		}
	}

	private JsonObject createTeamMemberObject(Player member, Team team) {
		JsonObject obj = new JsonObject();

		Color awtColor = team.getColor().asBungee().getColor();
		if (awtColor == null) awtColor = Color.WHITE;

		int rgb = awtColor.getRGB();

		obj.add("player_uuid", createUuidObject(member.getUniqueId()));

		obj.addProperty("adventure_json_player_name", toJson(
				Component.text(member.getName()).color(TextColor.color(rgb))
		));

		obj.add("marker_color", createColorObject(awtColor));

		obj.add("location", createLocationObject(member.getLocation()));
		return obj;
	}

	private void sendResetTeamMembers(Player player) {
		JsonObject message = new JsonObject();
		message.addProperty("@type", "type.googleapis.com/lunarclient.apollo.team.v1.ResetTeamMembersMessage");
		sendPacket(player, message);
	}

	private void sendPacket(Player player, JsonObject message) {
		byte[] bytes = message.toString().getBytes(StandardCharsets.UTF_8);
		player.sendPluginMessage(Main.plugin, APOLLO_CHANNEL, bytes);
	}

	private static JsonObject createUuidObject(UUID uuid) {
		JsonObject obj = new JsonObject();
		obj.addProperty("high64", Long.toUnsignedString(uuid.getMostSignificantBits()));
		obj.addProperty("low64", Long.toUnsignedString(uuid.getLeastSignificantBits()));
		return obj;
	}

	private static JsonObject createColorObject(Color color) {
		JsonObject obj = new JsonObject();
		obj.addProperty("color", color.getRGB());
		return obj;
	}

	private static JsonObject createLocationObject(Location location) {
		JsonObject obj = new JsonObject();
		obj.addProperty("world", location.getWorld().getName());
		obj.addProperty("x", location.getX());
		obj.addProperty("y", location.getY());
		obj.addProperty("z", location.getZ());
		return obj;
	}

	private static String toJson(Component component) {
		return GsonComponentSerializer.gson().serialize(component);
	}
}
