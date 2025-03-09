package com.booksaw.betterTeams.customEvents;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.post.PostTeamSendMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Deprecated
public class TeamMessageEvent extends PostTeamSendMessageEvent {
	@Deprecated
	public TeamMessageEvent(@NotNull Team team, @NotNull TeamPlayer sender, @NotNull String formattedMessage, @NotNull Collection<TeamPlayer> recipients) {
		super(team, sender, formattedMessage, recipients);
	}
}
