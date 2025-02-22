package com.booksaw.betterTeams.util;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.*;
import com.booksaw.betterTeams.message.MessageManager;
import dev.ceymikey.injection.DiscordPayload;
import dev.ceymikey.injection.EmbedBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This is where we listen for the events and define the info for the webhook.
 * There is some webhook events available not for all event
 *
 * @author ceymikey
 */
public class WebhookHandler implements Listener {

    private final String configURL = Main.plugin.getConfig().getString("hookURL");
    private final Boolean createHook = Main.plugin.getConfig().getBoolean("create-hook");
    private final Boolean disbandHook = Main.plugin.getConfig().getBoolean("disband-hook");
    private final Boolean pLeaveHook = Main.plugin.getConfig().getBoolean("player-left-hook");
    private final Boolean teamNameHook = Main.plugin.getConfig().getBoolean("team-nameChange-hook");
    private final Boolean teamChatEvent = Main.plugin.getConfig().getBoolean("team-chat");
    private Team team;
    private TeamPlayer teamPlayer;
    private String newTeamName;

    @EventHandler
    public void onTeamCreate(CreateTeamEvent e) {
        if (createHook) {
            team = e.getTeam();
            sendWebhookMessage(
                    MessageManager.getMessage("webhook.create.title"),
                    MessageManager.getMessage("webhook.create.description", team.getName()));
        }
    }

    @EventHandler
    public void onTeamDisband(DisbandTeamEvent e) {
        if (disbandHook) {
            team = e.getTeam();
            sendWebhookMessage(
                    MessageManager.getMessage("webhook.disband.title"),
                    MessageManager.getMessage("webhook.disband.description", team.getName()));
        }
    }

    @EventHandler
    public void onTeamPlayerLeft(PlayerLeaveTeamEvent e) {
        if (pLeaveHook) {
            team = e.getTeam();
            teamPlayer = e.getTeamPlayer();
            sendWebhookMessage(
                    MessageManager.getMessage("webhook.leave.title"),
                    MessageManager.getMessage("webhook.leave.description", teamPlayer.getPlayer(), team.getName()));
        }
    }

    @EventHandler
    public void onTeamRename(TeamNameChangeEvent e) {
        if (teamNameHook) {
            team = e.getTeam();
            newTeamName = e.getNewTeamName();
            sendWebhookMessage(
                    MessageManager.getMessage("webhook.rename.title"),
                    MessageManager.getMessage("webhook.rename.description", team.getName(), newTeamName));
        }
    }

    @EventHandler
    public void onTeamChat(TeamPreMessageEvent e) {
        if (teamChatEvent) {
            String eSender = e.getPlayer().getName();
            String message = e.getRawMessage();
            String teamName = e.getTeam().getName();
            sendWebhookMessage(
                    MessageManager.getMessage("webhook.chat.title"),
                    MessageManager.getMessage("webhook.chat.description", eSender, teamName, message));
        }
    }

    /* Sends the actual webhook message with the event info */
    public void sendWebhookMessage(String title, String description) {
        EmbedBuilder embed = new EmbedBuilder.Construct()
                .setUrl(configURL)
                .setTitle(title)
                .setDescription(description)
                .setColor(12370112)
                .build();
        DiscordPayload.inject(embed);
    }
}