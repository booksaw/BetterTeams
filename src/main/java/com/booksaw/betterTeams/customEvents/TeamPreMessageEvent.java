package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An event which is called before a message is sent from a {@link TeamPlayer} to members of their {@link Team}.
 */
public class TeamPreMessageEvent extends TeamPlayerEvent {

    private String rawMessage;
    private String format;
    private final Set<TeamPlayer> recipients = new HashSet<>();

    /**
     * Creates an event with the original data for the message.
     *
     * @param team the team which the message is being sent to
     * @param sender the sender of the message
     * @param rawMessage the contents of the message being sent (without formatting)
     * @param proposedFormat the proposed format for the message
     * @param recipients the current recipients of the message
     */
    public TeamPreMessageEvent(@NotNull Team team,
                               @NotNull TeamPlayer sender,
                               @NotNull String rawMessage,
                               @NotNull String proposedFormat,
                               @NotNull Collection<TeamPlayer> recipients) {
        super(team, sender);
        this.rawMessage = rawMessage;
        this.format = proposedFormat;
        this.recipients.addAll(recipients);
    }

    /**
     * @return The contents of the message which will be sent (without formatting).
     */
    public String getRawMessage() {
        return rawMessage;
    }

    /**
     * Sets the content for the message which will be sent (before formatting).
     *
     * @param rawMessage The new content of the message.
     */
    public void setRawMessage(@NotNull String rawMessage) {
        this.rawMessage = Objects.requireNonNull(rawMessage, "Team message cannot be null");
    }

    /**
     * @return The format which will be used to format the message.
     */
    public String getFormat() {
        return format;
    }

    /**
     * The format is as follows: '%s %s' -> '[player name] [message]'.
     *
     * @param newFormat the new format for the message following the format provided.
     * @apiNote If you do not understand how this works, research {@link String#format(String, Object...)} for details.
     */
    public void setFormat(@NotNull String newFormat) {
        this.format = Objects.requireNonNull(newFormat, "Team message format cannot be null");
    }

    /**
     * A mutable set of players which are going to receive a copy of the message.
     *
     * @return A mutable (modifiable) set of players which are going to receive a copy of the message.
     * @apiNote To change who will receive the message, edit this {@link Set}.
     */
    public Set<TeamPlayer> getRecipients() {
        return recipients;
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
