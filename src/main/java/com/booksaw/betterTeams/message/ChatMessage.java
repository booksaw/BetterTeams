package com.booksaw.betterTeams.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

import lombok.Getter;
import net.kyori.adventure.text.Component;

import static com.booksaw.betterTeams.message.Formatter.absoluteDeserialize;
import static com.booksaw.betterTeams.message.Formatter.absolutePlayerDeserialize;
import static com.booksaw.betterTeams.message.Formatter.setPlaceholders;
import static java.util.Objects.requireNonNull;

import java.util.Collection;

public class ChatMessage extends StaticComponentHolderMessage {

	@Getter
	private final Team team;

	@Getter
	private final TeamPlayer teamPlayer;

	@Getter
	private final Component spyMessage;

	public void sendSpyMessage(CommandSender recipient) {
		MessageManager.sendFullMessage(recipient, spyMessage);
	}
	
	public void sendSpyMessage(Collection<? extends CommandSender> recipients) {
		MessageManager.sendFullMessage(recipients, spyMessage);
	}

	private ChatMessage(Team team, TeamPlayer teamPlayer, Component message, Component spyMessage) {
		super(message);
		this.team = team;
		this.teamPlayer = teamPlayer;
		this.spyMessage = spyMessage;
	}

	public static ChatMessage teamChat(@NotNull Team team, @NotNull TeamPlayer teamPlayer, @NotNull String message) {
		return teamChat(team, teamPlayer, null, message);
	}

	public static ChatMessage teamChat(@NotNull Team team, @NotNull TeamPlayer teamPlayer, @Nullable String prefix, @NotNull String message) {
		if (!requireNonNull(team, "The provided team cannot be null.").getMembers().getClone().contains(requireNonNull(teamPlayer, "The provided team player cannot be null."))) {
			throw new IllegalArgumentException("The provided team player must be a member of the provided team.");
		}
		Player player = teamPlayer.getPlayer().getPlayer();
		if (player == null) {
			throw new IllegalArgumentException("The provided team player must be online.");
		}
		requireNonNull(message, "Team chat message cannot be null.");
		String syntax = requireNonNull(team.getTeamChatSyntax(teamPlayer), "Team chat syntax could not be found.");
		
		Component teamPreMessage = absoluteDeserialize(setPlaceholders(syntax, (prefix == null ? "" : prefix) + player.getDisplayName()));
		Component playerMessage = absolutePlayerDeserialize(message);
		Component spyPreMessage = absoluteDeserialize(MessageManager.getMessage(player, "spy.team", team.getName(), player.getName()));
		return new ChatMessage(team, teamPlayer, setPlaceholders(teamPreMessage, playerMessage, "{1}"), setPlaceholders(spyPreMessage, playerMessage, "{2}"));
	}

	public static ChatMessage allyChat(@NotNull Team team, @NotNull TeamPlayer teamPlayer, @Nullable String prefix, @NotNull String message) {
		if (!requireNonNull(team, "The provided team cannot be null.").getMembers().getClone().contains(requireNonNull(teamPlayer, "The provided team player cannot be null."))) {
			throw new IllegalArgumentException("The provided team player must be a member of the provided team.");
		}
		Player player = teamPlayer.getPlayer().getPlayer();
		if (player == null) {
			throw new IllegalArgumentException("The provided team player must be online.");
		}
		requireNonNull(message, "Team chat message cannot be null.");
		String syntax = requireNonNull(team.getAllyChatSyntax(teamPlayer), "Team chat syntax could not be found.");

		Component allyPreMessage = absoluteDeserialize(setPlaceholders(syntax, team.getDisplayName(), (prefix == null ? "" : prefix) + player.getDisplayName()));
		Component playerMessage = absolutePlayerDeserialize(message);
		Component spyPreMessage = absoluteDeserialize(MessageManager.getMessage(player, "spy.ally", team.getName(), player.getName()));
		return new ChatMessage(team, teamPlayer, setPlaceholders(allyPreMessage, playerMessage, "{2}"), setPlaceholders(spyPreMessage, playerMessage, "{2}"));
	}

	public static ChatMessage customSyntaxTeamChat(@NotNull Team team, @NotNull TeamPlayer teamPlayer, @Nullable String prefix, @NotNull String message, @NotNull String syntax) {
		if (!requireNonNull(team, "The provided team cannot be null.").getMembers().getClone().contains(requireNonNull(teamPlayer, "The provided team player cannot be null."))) {
			throw new IllegalArgumentException("The provided team player must be a member of the provided team.");
		}
		Player player = teamPlayer.getPlayer().getPlayer();
		if (player == null) {
			throw new IllegalArgumentException("The provided team player must be online.");
		}
		requireNonNull(message, "Team chat message cannot be null.");
		requireNonNull(syntax, "Team chat syntax cannot be null.");

		Component teamPreMessage = absoluteDeserialize(setPlaceholders(syntax, (prefix == null ? "" : prefix) + player.getDisplayName()));
		Component playerMessage = absolutePlayerDeserialize(message);
		Component spyPreMessage = absoluteDeserialize(MessageManager.getMessage(player, "spy.team", team.getName(), player.getName()));
		return new ChatMessage(team, teamPlayer, setPlaceholders(teamPreMessage, playerMessage, "{1}"), setPlaceholders(spyPreMessage, playerMessage, "{2}"));
	}

	@Override
	public void sendMessage(CommandSender recipient) {
		MessageManager.sendFullMessage(recipient, message);
	}

	@Override
	public void sendMessage(Collection<? extends CommandSender> recipients) {
		MessageManager.sendFullMessage(recipients, message);
	}

	@Override
	public void sendTitle(Player recipient) {
		MessageManager.sendFullTitle(recipient, message);
	}

	@Override
	public void sendTitle(Collection<Player> recipients) {
		MessageManager.sendFullTitle(recipients, message);
	}
}
