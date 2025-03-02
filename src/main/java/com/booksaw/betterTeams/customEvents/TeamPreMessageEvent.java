package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Deprecated
public class TeamPreMessageEvent extends TeamSendMessageEvent {
	@Deprecated
	public TeamPreMessageEvent(@NotNull Team team, @NotNull TeamPlayer sender, @NotNull String rawMessage, @NotNull String proposedFormat, @NotNull String senderNamePrefix, @NotNull Collection<TeamPlayer> recipients) {
		super(team, sender, rawMessage, proposedFormat, senderNamePrefix, recipients);
	}
}
