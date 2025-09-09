package com.booksaw.betterTeams.integrations;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.integrations.placeholder.TeamPlaceholderOptionsEnum;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LuckPermsManager implements ContextCalculator<Player> {

	private final LuckPerms luckPerms;

	public LuckPermsManager(LuckPerms luckPerms) {
		this.luckPerms = luckPerms;
	}

	@Override
	public void calculate(@NotNull Player player, @NotNull ContextConsumer consumer) {
		Team team = Team.getTeam(player);

		consumer.accept("bt_inteam", String.valueOf(team != null));
		if (team == null) {
			return;
		}
		TeamPlayer teamPlayer = team.getTeamPlayer(player);

		// Player specific
		addContext(consumer, "bt_rank", TeamPlaceholderOptionsEnum.RANK.applyPlaceholderProvider(team, teamPlayer), true);
		addContext(consumer, "bt_teamchat", TeamPlaceholderOptionsEnum.TEAMCHAT.applyPlaceholderProvider(team, teamPlayer));

		// Team info
		addContext(consumer, "bt_level", TeamPlaceholderOptionsEnum.LEVEL.applyPlaceholderProvider(team, teamPlayer));
		addContext(consumer, "bt_open", TeamPlaceholderOptionsEnum.OPEN.applyPlaceholderProvider(team, teamPlayer));
		addContext(consumer, "bt_pvp", TeamPlaceholderOptionsEnum.PVP.applyPlaceholderProvider(team, teamPlayer));
		addContext(consumer, "bt_hashome",  String.valueOf(team.getTeamHome() != null));

		// Leaderboard positions
		addContext(consumer, "bt_positionscore", TeamPlaceholderOptionsEnum.POSITIONSCORE.applyPlaceholderProvider(team, teamPlayer));
		addContext(consumer, "bt_positionbal", TeamPlaceholderOptionsEnum.POSITIONBAL.applyPlaceholderProvider(team, teamPlayer));
		addContext(consumer, "bt_positionmembers", TeamPlaceholderOptionsEnum.POSITIONMEMBERS.applyPlaceholderProvider(team, teamPlayer));
	}

	/**
	 * Helper method to avoid passing null values to the context consumer.
	 */
	private void addContext(ContextConsumer consumer, String key, String value) {
		addContext(consumer, key, value, false);
	}

	/**
	 * Helper method with optional lowercasing.
	 */
	private void addContext(ContextConsumer consumer, String key, String value, boolean toLowerCase) {
		if (value != null) {
			consumer.accept(key, toLowerCase ? value.toLowerCase() : value);
		}
	}

	@Override
	public @NotNull ContextSet estimatePotentialContexts() {
		ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
		builder.add("bt_inteam", "true");
		builder.add("bt_inteam", "false");
		builder.add("bt_pvp", "true");
		builder.add("bt_pvp", "false");
		builder.add("bt_open", "true");
		builder.add("bt_open", "false");
		builder.add("bt_level", "1");
		builder.add("bt_level", "2");

		for (int i = 1; i <= 3; i++) {
			builder.add("bt_positionscore", String.valueOf(i));
			builder.add("bt_positionbal", String.valueOf(i));
			builder.add("bt_positionmembers", String.valueOf(i));
		}

		for (PlayerRank rank : PlayerRank.values()) {
			builder.add("bt_rank", rank.name().toLowerCase());
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
