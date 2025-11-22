package com.booksaw.betterTeams.extensions.luckperms;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.integrations.placeholder.TeamPlaceholderOptionsEnum;
import com.booksaw.betterTeams.message.MessageManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LuckPermsManager implements ContextCalculator<Player> {

    private final LuckPerms luckPerms;
    private final FileConfiguration config;

    public LuckPermsManager(LuckPerms luckPerms, FileConfiguration config) {
        this.luckPerms = luckPerms;
        this.config = config;
    }

    @Override
    public void calculate(@NotNull Player player, @NotNull ContextConsumer consumer) {
        Team team = Team.getTeam(player);

        if (config.getBoolean("contexts.bt_inteam", true)) {
            consumer.accept("bt_inteam", String.valueOf(team != null));
        }

        if (team == null) {
            return;
        }
        TeamPlayer teamPlayer = team.getTeamPlayer(player);

        // Player specific
        if (config.getBoolean("contexts.bt_rank", true)) {
            addContext(consumer, "bt_rank", TeamPlaceholderOptionsEnum.RANK.applyPlaceholderProvider(team, teamPlayer));
        }
        if (config.getBoolean("contexts.bt_teamchat", true)) {
            addContext(consumer, "bt_teamchat", TeamPlaceholderOptionsEnum.TEAMCHAT.applyPlaceholderProvider(team, teamPlayer));
        }

        // Team info
        if (config.getBoolean("contexts.bt_level", true)) {
            addContext(consumer, "bt_level", TeamPlaceholderOptionsEnum.LEVEL.applyPlaceholderProvider(team, teamPlayer));
        }
        if (config.getBoolean("contexts.bt_open", true)) {
            addContext(consumer, "bt_open", TeamPlaceholderOptionsEnum.OPEN.applyPlaceholderProvider(team, teamPlayer));
        }
        if (config.getBoolean("contexts.bt_pvp", true)) {
            addContext(consumer, "bt_pvp", TeamPlaceholderOptionsEnum.PVP.applyPlaceholderProvider(team, teamPlayer));
        }
        if (config.getBoolean("contexts.bt_hashome", true)) {
            addContext(consumer, "bt_hashome", String.valueOf(team.getTeamHome() != null));
        }

        // Leaderboard positions
        if (config.getBoolean("contexts.bt_positionscore", true)) {
            addContext(consumer, "bt_positionscore", TeamPlaceholderOptionsEnum.POSITIONSCORE.applyPlaceholderProvider(team, teamPlayer));
        }
        if (config.getBoolean("contexts.bt_positionbal", true)) {
            addContext(consumer, "bt_positionbal", TeamPlaceholderOptionsEnum.POSITIONBAL.applyPlaceholderProvider(team, teamPlayer));
        }
        if (config.getBoolean("contexts.bt_positionmembers", true)) {
            addContext(consumer, "bt_positionmembers", TeamPlaceholderOptionsEnum.POSITIONMEMBERS.applyPlaceholderProvider(team, teamPlayer));
        }
    }

    /**
     * Helper method to avoid passing null values to the context consumer.
     */
    private void addContext(ContextConsumer consumer, String key, String value) {
        if (value != null) {
            consumer.accept(key, value);
        }
    }

    private void addTrueFalse(ImmutableContextSet.Builder builder, String key) {
        builder.add(key, "true");
        builder.add(key, "false");
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        if (config.getBoolean("contexts.bt_inteam", true)) {
            addTrueFalse(builder, "bt_inteam");
        }
        if (config.getBoolean("contexts.bt_pvp", true)) {
            addTrueFalse(builder, "bt_pvp");
        }
        if (config.getBoolean("contexts.bt_open", true)) {
            addTrueFalse(builder, "bt_open");
        }
        if (config.getBoolean("contexts.bt_hashome", true)) {
            addTrueFalse(builder, "bt_hashome");
        }

        if (config.getBoolean("contexts.bt_teamchat", true)) {
            builder.add("bt_teamchat", MessageManager.getMessage("placeholder.teamChat"));
            builder.add("bt_teamchat", MessageManager.getMessage("placeholder.allyChat"));
            builder.add("bt_teamchat", MessageManager.getMessage("placeholder.globalChat"));
        }

        if (config.getBoolean("contexts.bt_rank", true)) {
            builder.add("bt_rank", MessageManager.getMessage("placeholder.owner"));
            builder.add("bt_rank", MessageManager.getMessage("placeholder.admin"));
            builder.add("bt_rank", MessageManager.getMessage("placeholder.default"));
        }

        if (config.getBoolean("contexts.bt_level", true)) {
            builder.add("bt_level", "1");
            builder.add("bt_level", "2");
        }

        boolean score = config.getBoolean("contexts.bt_positionscore", true);
        boolean bal = config.getBoolean("contexts.bt_positionbal", true);
        boolean members = config.getBoolean("contexts.bt_positionmembers", true);

        for (int i = 1; i <= 3; i++) {
            if (score) builder.add("bt_positionscore", String.valueOf(i));
            if (bal) builder.add("bt_positionbal", String.valueOf(i));
            if (members) builder.add("bt_positionmembers", String.valueOf(i));
        }

        return builder.build();
    }

    public void register() {
        this.luckPerms.getContextManager().registerCalculator(this);
    }

    public void unregister() {
        this.luckPerms.getContextManager().unregisterCalculator(this);
    }
}
