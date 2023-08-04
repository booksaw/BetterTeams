package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.google.common.collect.ImmutableSet;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TeamMessageEvent extends TeamPlayerEvent {

    private final String formattedMessage;
    private final Set<TeamPlayer> recipients = new HashSet<>();

    /**
     * Creates an event with the original data for the message.
     *
     * @param team the team which the message is being sent to
     * @param sender the sender of the message
     * @param formattedMessage the message which has been sent (with the included formatting)
     * @param recipients the current recipients of the message
     */
    public TeamMessageEvent(@NotNull Team team,
                               @NotNull TeamPlayer sender,
                               @NotNull String formattedMessage,
                               @NotNull Collection<TeamPlayer> recipients) {
        super(team, sender);
        this.formattedMessage = formattedMessage;
        this.recipients.addAll(recipients);
    }

    /**
     * @return The contents of the message which has been sent (with formatting).
     */
    public String getFormattedMessage() {
        return formattedMessage;
    }

    /**
     * An immutable set of players which have received a copy of the message.
     *
     * @return An immutable (unmodifiable) set of players which have already received a copy of the message.
     * @apiNote To change the recipients of a team message, use {@link TeamPreMessageEvent}.
     */
    public @Unmodifiable Set<TeamPlayer> getRecipients() {
        return ImmutableSet.copyOf(recipients);
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
