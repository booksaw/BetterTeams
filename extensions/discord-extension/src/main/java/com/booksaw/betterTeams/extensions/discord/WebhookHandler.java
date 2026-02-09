package com.booksaw.betterTeams.extensions.discord;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.post.*;
import com.booksaw.betterTeams.message.MessageManager;
import dev.ceymikey.injection.DiscordPayload;
import dev.ceymikey.injection.EmbedBuilder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This is where we listen for the events and define the info for the webhook.
 * There is some webhook events available not for all event
 *
 * @author ceymikey
 */
public class WebhookHandler implements Listener {

	private final DiscordExtension extension;
	private final String configURL;
	private final Boolean createHook;
	private final Boolean disbandHook;
	private final Boolean pLeaveHook;
	private final Boolean teamNameHook;
	private final Boolean teamChatEvent;

	public WebhookHandler(DiscordExtension extension) {
		this.extension = extension;
		FileConfiguration config = extension.getConfig().getConfig();
		extension.getMessages(); // for create message file

		configURL = config.getString("hookURL");
		createHook = config.getBoolean("create-hook");
		disbandHook = config.getBoolean("disband-hook");
		pLeaveHook = config.getBoolean("player-left-hook");
		teamNameHook = config.getBoolean("team-nameChange-hook");
		teamChatEvent = config.getBoolean("team-chat");
	}

	private String getMessage(String path, Object... replacements) {
		return extension.getMessages().get(path, replacements);
	}

	@EventHandler
	public void onTeamCreate(PostCreateTeamEvent e) {
		if (createHook) {
			Team team = e.getTeam();
			String playerOrUnknown = e.getPlayer() != null ? e.getPlayer().getName() : getMessage("webhook.unknownPlayer");
			sendWebhookMessage(
					getMessage("webhook.create.title", team.getName()),
					getMessage("webhook.create.description", team.getName(), playerOrUnknown));
		}
	}

	@EventHandler
	public void onTeamDisband(PostDisbandTeamEvent e) {
		if (disbandHook) {
			Team team = e.getTeam();
			String playerOrUnknown = e.getPlayer() != null ? e.getPlayer().getName() : getMessage("webhook.unknownPlayer");
			sendWebhookMessage(
					getMessage("webhook.disband.title", team.getName()),
					getMessage("webhook.disband.description", team.getName(), playerOrUnknown));
		}
	}

	@EventHandler
	public void onTeamPlayerLeft(PostPlayerLeaveTeamEvent e) {
		if (pLeaveHook) {
			Team team = e.getTeam();
			TeamPlayer teamPlayer = e.getTeamPlayer();
			sendWebhookMessage(
					getMessage("webhook.leave.title", teamPlayer.getPlayer().getName()),
					getMessage("webhook.leave.description", teamPlayer.getPlayer().getName(), team.getName()));
		}
	}

	@EventHandler
	public void onTeamRename(PostTeamNameChangeEvent e) {
		if (teamNameHook) {
			Team team = e.getTeam();
			String newTeamName = e.getNewTeamName();
			String playerOrUnknown = e.getPlayer() != null ? e.getPlayer().getName() : getMessage("webhook.unknownPlayer");
			sendWebhookMessage(
					getMessage("webhook.rename.title", newTeamName),
					getMessage("webhook.rename.description", team.getName(), newTeamName, playerOrUnknown));
		}
	}

	@EventHandler
	public void onTeamChat(PostTeamSendMessageEvent e) {
		if (teamChatEvent) {
			String eSender = e.getPlayer().getName();
			String message = e.getFormattedMessage();
			String teamName = e.getTeam().getName();
			sendWebhookMessage(
					getMessage("webhook.chat.title", eSender, teamName),
					getMessage("webhook.chat.description", message));
		}
	}

	/* Sends the actual webhook message with the event info */
	public void sendWebhookMessage(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder.Construct()
				.setUrl(configURL)
				.setTitle(ChatColor.stripColor(title))
				.setDescription(ChatColor.stripColor(description))
				.setColor(12370112)
				.build();
		DiscordPayload.inject(embed);
	}
}