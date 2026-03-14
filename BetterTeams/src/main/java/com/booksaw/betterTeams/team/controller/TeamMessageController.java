package com.booksaw.betterTeams.team.controller;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.TeamSendMessageEvent;
import com.booksaw.betterTeams.customEvents.post.PostTeamSendMessageEvent;
import com.booksaw.betterTeams.message.ChatMessage;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.text.LegacyTextUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TeamMessageController {

	public enum TeamMessageType {
		TEAM_CHAT_MESSAGE("chat.syntax", "spy.team"),
		ALLY_CHAT_MESSAGE("allychat.syntax", "spy.ally");

		public final String chatFormat;
		public final String chatSpyFormat;

		TeamMessageType(String chatFormat, String chatspyFormat) {
			this.chatFormat = chatFormat;
			this.chatSpyFormat = chatspyFormat;
		}
	}

	private final Team team;

	public void sendTeamChatMessage(TeamPlayer sender, String message) {
		Set<TeamPlayer> recipients = team.getMembers().getClone();

		sendTeamMessage(sender, message, recipients, TeamMessageType.TEAM_CHAT_MESSAGE);
	}

	public void sendAllyChatMessage(TeamPlayer sender, String message) {
		Set<TeamPlayer> recipients = team.getMembers().getClone();
		team.getAllies().getClone().stream().map(Team::getTeam).filter(Objects::nonNull)
				.forEach(team -> recipients.addAll(team.getMembers().getClone()));

		sendTeamMessage(sender, message, recipients, TeamMessageType.ALLY_CHAT_MESSAGE);
	}

	private void sendTeamMessage(TeamPlayer sender, String message, Set<TeamPlayer> recipients, TeamMessageType messageType) {
		recipients.removeIf(teamPlayer -> !teamPlayer.getPlayer().isOnline()); // Offline players won't be recipients
		String format = getChatSyntax(sender, messageType);

		// Notify third party plugins that a team message is going to be sent
		TeamSendMessageEvent teamSendMessageEvent = new TeamSendMessageEvent(team, sender, message, format,
				sender.getPlayerPrefix() + getPreviousChatColor(format), recipients, messageType);
		Bukkit.getPluginManager().callEvent(teamSendMessageEvent);

		// Process any updates after the event has been dispatched
		if (teamSendMessageEvent.isCancelled()) {
			Main.plugin.getLogger().log(Level.FINE, "Team send message event is cancelled");
			return;
		}

		message = teamSendMessageEvent.getRawMessage();
		format = teamSendMessageEvent.getFormat();
		String prefix = teamSendMessageEvent.getSenderNamePrefix();
		recipients = teamSendMessageEvent.getRecipients();


		ChatMessage chatMsg = sendApprovedTeamMessage(sender, prefix, message, format, recipients, messageType);

		String fMessage = LegacyTextUtils.serialize(chatMsg.getMessage());
		// Notify third party plugins that a message has been dispatched
		Bukkit.getPluginManager().callEvent(new PostTeamSendMessageEvent(team, sender, fMessage, recipients, messageType));
	}

	/**
	 * Used to get the chat syntax and apply placeholders when possible
	 *
	 * @param sender - The team player who sent the command
	 */

	public String getChatSyntax(TeamPlayer sender, TeamMessageType messageType) {
		if (sender != null && sender.getPlayer() != null && sender.getPlayer().isOnline() && (sender.getPlayer().getPlayer() != null)) {
			return MessageManager.getMessage(sender.getPlayer().getPlayer(), "allychat.syntax").replace("$name$", "{1}").replace("$message$", "{2}");
		}

		return MessageManager.getMessage(messageType.chatFormat).replace("$name$", "{1}").replace("$message$", "{2}");
	}

	private static @NotNull ChatColor getPreviousChatColor(String toTest) {
		Matcher matcher = Pattern.compile("\\{\\d+}").matcher(toTest);
		if (matcher.find()) {
			int value = matcher.start();
			if (value > 3) {
				for (int i = value; i >= 0; i--) {
					if (toTest.charAt(i) == ChatColor.COLOR_CHAR) {
						ChatColor returnTo = ChatColor.getByChar(toTest.charAt(i + 1));
						if (returnTo != null) {
							return returnTo;
						}
					}
				}
			}
		}

		return ChatColor.RESET;
	}

	private Collection<CommandSender> getOnlineChatSpyPlayers() {
		return Main.plugin.chatManagement.spy.stream()
				.filter(Objects::nonNull)
				.filter(temp -> !(temp instanceof Player && team.getTeamPlayer((Player) temp) != null))
				.collect(Collectors.toList());
	}

	private ChatMessage sendApprovedTeamMessage(TeamPlayer sender, String prefix, String message, String format, Collection<TeamPlayer> recipients, TeamMessageType messageType) {
		Collection<Player> playerRecipients = recipients.stream().map(r -> r.getPlayer().getPlayer())
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		ChatMessage chatMsg = ChatMessage.teamChat(team, sender, prefix, message, format, messageType);
		chatMsg.sendMessage(playerRecipients);
		chatMsg.sendSpyMessage(getOnlineChatSpyPlayers());

		if (Team.getTeamManager().isLogChat()) {
			MessageManager.sendFullMessage(Bukkit.getConsoleSender(), chatMsg.getMessage());
		}
		return chatMsg;
	}

}
