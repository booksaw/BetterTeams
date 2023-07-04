package com.booksaw.betterTeams.integrations;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.message.MessageManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This class is used to set the placeholder values for placeholder API
 *
 * @author booksaw
 */
public class TeamPlaceholders extends PlaceholderExpansion {
    private final Plugin plugin;

    public TeamPlaceholders(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "betterTeams";
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        String[] args = identifier.toLowerCase().split("_");

        switch (args[0]) {
            case "position":
                return this.position(args);
            case "members": {
                Team team = Team.getTeam(args[1]);

                if (team == null) {
                    return MessageManager.getMessage("placeholder.noTeam");
                }

                return String.valueOf(identifier.endsWith("_online") ? team.getMembers().size() : team.getOnlineMembers().size());
            }
            case "teamscore": {
                if (args.length < 2) {
                    return null;
                }

                Integer place = this.parseInt(args[1]);
                if (place == null) {
                    return null;
                }
                place -= 1;

                String[] teams = Team.getTeamManager().sortTeamsByScore();
                if (place < 0 || place >= teams.length) {
                    return null;
                }

                return String.valueOf(Team.getTeam(teams[place]).getName());
            }
            case "teamscoreno": {
                if (args.length < 2) {
                    return null;
                }

                Integer place = this.parseInt(args[1]);
                if (place == null) {
                    return null;
                }
                place -= 1;

                String[] teams = Team.getTeamManager().sortTeamsByScore();
                if (place < 0 || place >= teams.length) {
                    return null;
                }

                return String.valueOf(Team.getTeam(teams[place]).getScore());
            }
        }

        if (player == null) {
            return "";
        }

        if (identifier.equalsIgnoreCase(args[0])) {
            if (Team.getTeamManager().isInTeam(player)) {
                return MessageManager.getMessage("placeholder.inteam");
            } else {
                return MessageManager.getMessage("placeholder.notinteam");
            }
        }

        Team team = Team.getTeam(player);
        if (team == null) {
            return MessageManager.getMessage("placeholder.noTeam");
        }

        switch (args[0]) {
            case "name":
                return String.format(MessageManager.getMessage(player, "placeholder.name"), team.getName());
            case "tag":
                return String.format(MessageManager.getMessage(player, "placeholder.tag"), team.getTag());
            case "displayname":
                return String.format(MessageManager.getMessage(player, "placeholder.displayname"), team.getColor() + team.getTag());
            case "description": {
                if (team.getDescription() == null || team.getDescription().isEmpty()) {
                    return MessageManager.getMessage("placeholder.noDescription");
                }
                return team.getDescription();
            }
            case "open":
                return String.valueOf(team.isOpen());
            case "money":
                return String.format(MessageManager.getMessage(player, "placeholder.money"), team.getBalance());
            case "score":
                return String.valueOf(team.getScore());
            case "rank": {
                switch (Objects.requireNonNull(team.getTeamPlayer(player)).getRank()) {
                    case ADMIN:
                        return MessageManager.getMessage(player, "placeholder.admin");
                    case DEFAULT:
                        return MessageManager.getMessage(player, "placeholder.default");
                    case OWNER:
                        return MessageManager.getMessage(player, "placeholder.owner");
                }
            }
            case "color":
                return String.valueOf(team.getColor());
            case "online":
                return String.valueOf(team.getOnlineMembers().size());
            case "title": {
                TeamPlayer tp = team.getTeamPlayer(player);
                if (tp == null) {
                    return null;
                }
                if (tp.getTitle() == null || tp.getTitle().isEmpty()) {
                    return MessageManager.getMessage("placeholder.noTitle");
                }
                return tp.getTitle();
            }
            case "onlinelist":
                return team.getMembers().getOnlinePlayersString();
            case "offlinelist":
                return team.getMembers().getOfflinePlayersString();
        }

        return null;
    }

    public String position(String[] args) {
        if (args.length < 3) {
            return null;
        }

        Integer place = this.parseInt(args[2]);
        if (place == null) {
            return null;
        }
        place -= 1;

        if (place < 0) {
            return null;
        }

        String[] teams = Team.getTeamManager().sortTeamsByScore();
        if (teams.length <= place) {
            return null;
        }

        Team team = Team.getTeamByName(teams[place]);

        switch (args[1]) {
            case "name":
                return team.getName();
            case "description":
                return team.getDescription();
            case "tag":
                return team.getTag();
            case "displayname":
                return team.getDisplayName();
            case "open":
                return String.valueOf(team.isOpen());
            case "balance":
                return team.getBalance();
            case "score":
                return String.valueOf(team.getScore());
            case "color":
                return String.valueOf(team.getColor());
            case "offlinelist":
                return team.getMembers().getOfflinePlayersString();
            case "onlinelist":
                return team.getMembers().getOnlinePlayersString();
            default:
                return null;
        }
    }

    public Integer parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
