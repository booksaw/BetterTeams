package com.booksaw.betterTeams.util;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This is where we listen for the events and define the info for the webhook.
 * There is some webhook events available not for all event
 * The actual injecting logic is done at 'com.booksaw.betterTeams.util'
 *
 * @author ceymikey
 */
public class WebhookPayload implements Listener {

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
            sendWebhookMessage("TEAM CREATED", "Somebody created the team " + team.getName());
        }
    }

    @EventHandler
    public void onTeamDisband(DisbandTeamEvent e) {
        if (disbandHook) {
            team = e.getTeam();
            sendWebhookMessage("TEAM DISBAND", "Somebody deleted the team " + team.getName());
        }
    }

    @EventHandler
    public void onTeamPlayerLeft(PlayerLeaveTeamEvent e) {
        if (pLeaveHook) {
            team = e.getTeam();
            teamPlayer = e.getTeamPlayer();
            sendWebhookMessage("TEAM MEMBER LEFT", teamPlayer.getPlayer() + " has left the team `" + team.getName() + "`");
        }
    }

    @EventHandler
    public void onTeamRename(TeamNameChangeEvent e) {
        if (teamNameHook) {
            team = e.getTeam();
            newTeamName = e.getNewTeamName();
            sendWebhookMessage("TEAM NAME CHANGE", "Changed name of team " + team.getName() +  " to " + newTeamName);
        }
    }

    @EventHandler
    public void onTeamChat(TeamPreMessageEvent e) {
        if (teamChatEvent) {
            String eSender = e.getPlayer().getName();
            String message = e.getRawMessage();
            sendWebhookMessage("Team chat from " + eSender, message);
        }
    }

    /**
     * Tried out a new approach here
     * rather than using an existing library.
     */
    public void sendWebhookMessage(String title, String description) {
        DiscordHookUtil payload = new DiscordHookUtil.Builder()
                .setUrl(configURL)
                .setTitle(title)
                .setDescription(description)
                .setColor(12370112)
                .inject();
        payload.injectJsonPayload();
    }
}
