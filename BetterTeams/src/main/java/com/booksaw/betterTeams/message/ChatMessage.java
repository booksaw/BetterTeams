package com.booksaw.betterTeams.message;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.team.controller.TeamMessageController;
import com.booksaw.betterTeams.text.Formatter;
import com.booksaw.betterTeams.util.ComponentUtil;
import com.booksaw.betterTeams.util.StringUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

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

	public static ChatMessage teamChat(@NotNull Team team, @NotNull TeamPlayer teamPlayer, @Nullable String prefix, @NotNull String message, @NotNull String syntax, TeamMessageController.TeamMessageType messageType) {
		if (!requireNonNull(team, "The provided team cannot be null.").getMembers().getClone().contains(requireNonNull(teamPlayer, "The provided team player cannot be null."))) {
			throw new IllegalArgumentException("The provided team player must be a member of the provided team.");
		}
		Player player = teamPlayer.getPlayer().getPlayer();
		if (player == null) {
			throw new IllegalArgumentException("The provided team player must be online.");
		}
		requireNonNull(message, "Team chat message cannot be null.");
		requireNonNull(syntax, "Team chat syntax cannot be null.");

		Component teamPreMessage = Formatter.absolute().process(StringUtil.setPlaceholders(syntax, (prefix == null ? "" : prefix) + player.getDisplayName()));
		Component playerMessage = Formatter.player(player).process(message);
		Component spyPreMessage = Formatter.absolute().process(MessageManager.getMessage(player, messageType.chatSpyFormat, team.getName(), player.getName()));
		return new ChatMessage(team, teamPlayer, ComponentUtil.setPlaceholders(teamPreMessage, playerMessage, "{1}"), ComponentUtil.setPlaceholders(spyPreMessage, playerMessage, "{2}"));
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
