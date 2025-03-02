package com.booksaw.betterTeams.customEvents.post;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.TeamPlayerEvent;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Set;

/**
 * An event which is called after a team message has been sent (notification only).
 */
@Getter
public class PostTeamMessageEvent extends TeamPlayerEvent {

	/// The contents of the message which has been sent (with formatting).
	private final String formattedMessage;
	/// An immutable set of players which have received a copy of the message.
	@Unmodifiable
	private final Set<TeamPlayer> recipients;

	/**
	 * Creates an event with the original data for the message.
	 *
	 * @param team             the team which the message is being sent to
	 * @param sender           the sender of the message
	 * @param formattedMessage the message which has been sent (with the included formatting)
	 * @param recipients       the current recipients of the message
	 */
	public PostTeamMessageEvent(@NotNull Team team,
								@NotNull TeamPlayer sender,
								@NotNull String formattedMessage,
								@NotNull Collection<TeamPlayer> recipients) {
		super(team, sender, true);

		this.formattedMessage = formattedMessage;
		this.recipients = ImmutableSet.copyOf(recipients);
	}

	/**
	 * @return The player who sent this message to their team.
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
