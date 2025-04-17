package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.message.Formatter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An event which is called before a message is sent from a {@link TeamPlayer} to members of their {@link Team}.
 */
@Getter
@Setter
public class TeamSendMessageEvent extends TeamPlayerEvent {

	/// The contents of the message which will be sent (without formatting).
	private String rawMessage;
	/**
	 * The format which will be used to format the message.
	 *
	 * @apiNote {0} is replaced with the player name and {1} is the message
	 */
	private String format;
	/// The prefix that is put before the name of the sender in the message format.
	private String senderNamePrefix;
	/**
	 * A mutable set of players which are going to receive a copy of the message.
	 *
	 * @apiNote To change who will receive the message, edit this {@link Set}.
	 */
	private final Set<TeamPlayer> recipients = new HashSet<>();

	/**
	 * Creates an event with the original data for the message.
	 *
	 * @param team             the team which the message is being sent to
	 * @param sender           the sender of the message
	 * @param rawMessage       the contents of the message being sent (without formatting)
	 * @param proposedFormat   the proposed format for the message
	 * @param senderNamePrefix the prefix which will be appended to the sender's name in the formatted message
	 * @param recipients       the current recipients of the message
	 */
	public TeamSendMessageEvent(@NotNull Team team,
								@NotNull TeamPlayer sender,
								@NotNull String rawMessage,
								@NotNull String proposedFormat,
								@NotNull String senderNamePrefix,
								@NotNull Collection<TeamPlayer> recipients) {
		super(team, sender, true);
		this.rawMessage = rawMessage;
		this.format = proposedFormat;
		this.senderNamePrefix = senderNamePrefix;
		this.recipients.addAll(recipients);
	}

	/**
	 * Using {@link #getFormat()} and {@link #getRawMessage()}, this method will format the raw message and return the
	 * formatted message using the current format.
	 *
	 * @return The formatted message using the current format and raw message.
	 */
	public String getFormattedMessage() {
		return Formatter.setPlaceholders(getFormat(), getFormattedSenderName(), getRawMessage());
	}

	/**
	 * @return The name of the sender as it will be displayed in the message format ('[prefix][name]').
	 * @apiNote The name of the sender is retrieved using {@link Player#getDisplayName()} and can be edited accordingly.
	 */
	public String getFormattedSenderName() {
		return getSenderNamePrefix() + Objects.requireNonNull(getSender().getPlayer().getPlayer()).getDisplayName();
	}

	/**
	 * @return The player sending this message to their team.
	 * @apiNote A more readable overload of {@link #getTeamPlayer()}.
	 */
	public TeamPlayer getSender() {
		return getTeamPlayer();
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

}
