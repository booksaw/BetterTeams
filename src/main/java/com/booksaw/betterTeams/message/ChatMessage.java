package com.booksaw.betterTeams.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
	private final Component spyMessage;

	public void sendSpyMessage(CommandSender recipient) {
		MessageManager.sendFullMessage(recipient, spyMessage);
	}
	
	public void sendSpyMessage(Collection<? extends CommandSender> recipients) {
		MessageManager.sendFullMessage(recipients, spyMessage);
	}

	private ChatMessage(Component message, Component spyMessage) {
		super(message);
		this.spyMessage = spyMessage;
	}

	public static ChatMessage teamChat(@NotNull Team team, @NotNull TeamPlayer teamPlayer, String prefix, String message) {
		requireNonNull(team);
		Player player = requireNonNull(teamPlayer.getPlayer().getPlayer());
		Component teamPreMessage = absoluteDeserialize(setPlaceholders(team.getTeamChatSyntax(teamPlayer), (prefix == null ? "" : prefix) + player.getDisplayName()));
		Component playerMessage = absolutePlayerDeserialize(message);
		Component spyPreMessage = absoluteDeserialize(MessageManager.getMessage(player, "spy.team", team.getName(), player.getName()));
		return new ChatMessage(setPlaceholders(teamPreMessage, playerMessage, "{1}"), setPlaceholders(spyPreMessage, playerMessage, "{2}"));
	}

	public static ChatMessage allyChat(@NotNull Team team, @NotNull TeamPlayer teamPlayer, String prefix, String message) {
		requireNonNull(team);
		Player player = requireNonNull(teamPlayer.getPlayer().getPlayer());
		Component allyPreMessage = absoluteDeserialize(setPlaceholders(team.getAllyChatSyntax(teamPlayer), team.getDisplayName(), (prefix == null ? "" : prefix) + player.getDisplayName()));
		Component playerMessage = absolutePlayerDeserialize(message);
		Component spyPreMessage = absoluteDeserialize(MessageManager.getMessage(player, "spy.ally", team.getName(), player.getName()));
		return new ChatMessage(setPlaceholders(allyPreMessage, playerMessage, "{2}"), setPlaceholders(spyPreMessage, playerMessage, "{2}"));
	}

	public static ChatMessage customFormatTeamChat(@NotNull Team team, @NotNull TeamPlayer teamPlayer, String message, String format, String prefix) {
		requireNonNull(team);
		Player player = requireNonNull(teamPlayer.getPlayer().getPlayer());
		Component teamPreMessage = absoluteDeserialize(setPlaceholders(format, (prefix == null ? "" : prefix) + player.getDisplayName()));
		Component playerMessage = absolutePlayerDeserialize(message);
		Component spyPreMessage = absoluteDeserialize(MessageManager.getMessage(player, "spy.team", team.getName(), player.getName()));
		return new ChatMessage(setPlaceholders(teamPreMessage, playerMessage, "{2}"), setPlaceholders(spyPreMessage, playerMessage, "{2}"));
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
