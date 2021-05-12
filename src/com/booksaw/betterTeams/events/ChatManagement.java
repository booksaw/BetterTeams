package com.booksaw.betterTeams.events;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatManagement implements Listener {

    private static PrefixType doPrefix;
    public final List<CommandSender> spy = new ArrayList<>();

    public static void enable() {
        doPrefix = PrefixType.getType(Main.plugin.getConfig().getString("prefix"));
    }

    /**
     * This detects when the player speaks and either adds a prefix or puts the
     * message in the team chat
     *
     * @param event the chat event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) {
            return;
        }

        Player p = event.getPlayer();
        Team team = Team.getTeam(p);

        if (team == null) {
            return;
        }

        TeamPlayer teamPlayer = team.getTeamPlayer(p);

        if ((Objects.requireNonNull(teamPlayer).isInTeamChat() || teamPlayer.isInAllyChat())
                && (event.getMessage().startsWith("!") && event.getMessage().length() > 1)) {
            event.setMessage(event.getMessage().substring(1));
        } else if (teamPlayer.isInTeamChat()) {
            // player is sending to team chat
            event.setCancelled(true);

            team.sendMessage(teamPlayer, event.getMessage());
            // Used as some chat plugins do not accept when a message is cancelled
            event.setMessage("");
            event.setFormat("");
            return;
        } else if (teamPlayer.isInAllyChat()) {
            // player is sending to ally chat
            event.setCancelled(true);

            team.sendAllyMessage(teamPlayer, event.getMessage());
            // Used as some chat plugins do not accept when a message is cancelled
            event.setMessage("");
            event.setFormat("");
            return;
        }

        if (doPrefix != PrefixType.NONE) {
            event.setFormat(doPrefix.getUpdatedFormat(p, event.getFormat(), team));
//				event.setFormat(ChatColor.AQUA + "[" + team.getName() + "] " + ChatColor.WHITE + event.getFormat());
        }

    }

    @EventHandler
    public void spyQuit(PlayerQuitEvent e) {
        spy.remove(e.getPlayer());
    }

    enum PrefixType {
        NONE, NAME, TAG;

        public static PrefixType getType(String str) {
            str = str.toLowerCase().trim();
            switch (str) {
                case "name":
                case "true":
                    return NAME;
                case "tag":
                    return TAG;
                default:
                    return NONE;
            }
        }

        public String getUpdatedFormat(Player p, String format, Team team) {
            switch (this) {
                case NAME:
                    String syntax = MessageManager.getMessage(p, "prefixSyntax");
                    return String.format(syntax, team.getDisplayName(), format);
                case TAG:
                    syntax = MessageManager.getMessage(p, "prefixSyntax");
                    return String.format(syntax, team.getColor() + team.getTag(), format);
                default:
                    return format;
            }
        }

    }

}
