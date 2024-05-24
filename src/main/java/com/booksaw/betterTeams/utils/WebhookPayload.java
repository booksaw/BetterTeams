package com.booksaw.betterTeams.utils;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This is where we send a basic payload
 * to discord with the required info.
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
    public Team team;
    public TeamPlayer teamPlayer;
    public String newTeamName;

    /**
     * There is 1 webhook for not every single event
     * but I've included some basic ones right here.
     * Will add more in the future
     * @param e
     */
    @EventHandler
    public void onTeamCreate(CreateTeamEvent e) {
        if (createHook) {
            team = e.getTeam();
            String jsonPayload = String.format("{\"content\":\"\",\"embeds\":[{\"title\":\"TEAM CREATED\",\"description\":\"Somebody created the team `%s`\",\"color\":12370112}]}", team.getName());
            sendWebhookMessage(jsonPayload);
        }
    }

    @EventHandler
    public void onTeamDisband(DisbandTeamEvent e) {
        if (disbandHook) {
            team = e.getTeam();
            String jsonPayload = String.format("{\"content\":\"\",\"embeds\":[{\"title\":\"TEAM DISBAND\",\"description\":\"Somebody deleted the team `%s`\",\"color\":12370112}]}", team.getName());
            sendWebhookMessage(jsonPayload);
        }
    }

    @EventHandler
    public void onTeamPlayerLeft(PlayerLeaveTeamEvent e) {
        if (pLeaveHook) {
            team = e.getTeam();
            teamPlayer = e.getTeamPlayer();
            String jsonPayload = String.format("{\"content\":\"\",\"embeds\":[{\"title\":\"TEAM MEMBER LEFT\",\"description\":\"`%s` has left the team `%s\",\"color\":12370112}]}", teamPlayer.getPlayer(), team.getName());
            sendWebhookMessage(jsonPayload);
        }
    }

    @EventHandler
    public void onTeamRename(TeamNameChangeEvent e) {
        if (teamNameHook) {
            team = e.getTeam();
            newTeamName = e.getNewTeamName();
            String jsonPayload = String.format("{\"content\":\"\",\"embeds\":[{\"title\":\"TEAM NAME CHANGE\",\"description\":\"Changed name of team `%s` to `%s`\",\"color\":12370112}]}", team.getName(), newTeamName);
            sendWebhookMessage(jsonPayload);
        }
    }

    @EventHandler
    public void onTeamChat(TeamPreMessageEvent e) {
        if (teamChatEvent) {
            String eSender = e.getPlayer().getName();
            String message = e.getRawMessage();
            String jsonPayload = String.format("{\"content\":\"\",\"embeds\":[{\"title\":\"Team chat from `%s`\",\"description\":\"%s\",\"color\":12370112}]}", eSender, message);
            sendWebhookMessage(jsonPayload);
        }
    }

    /**
     * Tried out a new approach here
     * rather than using an existing library.
     * @param jsonPayload
     */
    public void sendWebhookMessage(String jsonPayload) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(configURL);
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(new StringEntity(jsonPayload));

            httpClient.execute(httpPost);
            httpClient.close();
        } catch (Exception e) {
            System.out.println("Failed to send webhook message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
