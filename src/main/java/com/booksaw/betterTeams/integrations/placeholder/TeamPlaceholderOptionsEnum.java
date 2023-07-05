/**
 * 
 */
package com.booksaw.betterTeams.integrations.placeholder;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.integrations.placeholder.provider.ColorPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.DescriptionPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.DisplayNamePlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.LevelPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.MembersPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.MoneyPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.NamePlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.OfflineListPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.OnlineListPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.OnlinePlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.OpenPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.RankPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.ScorePlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.TagPlaceholderProvider;
import com.booksaw.betterTeams.integrations.placeholder.provider.TitlePlaceholderProvider;

/**
 * @author booksaw
 *
 */
public enum TeamPlaceholderOptionsEnum {
	NAME(new NamePlaceholderProvider()), TAG(new TagPlaceholderProvider()),
	DISPLAYNAME(new DisplayNamePlaceholderProvider()), DESCRIPTION(new DescriptionPlaceholderProvider()),
	OPEN(new OpenPlaceholderProvider()), SCORE(new ScorePlaceholderProvider()), MONEY(new MoneyPlaceholderProvider()),
	RANK(new RankPlaceholderProvider()), COLOR(new ColorPlaceholderProvider()), TITLE(new TitlePlaceholderProvider()),
	ONLINELIST(new OnlineListPlaceholderProvider()), OFFLINELIST(new OfflineListPlaceholderProvider()),
	ONLINE(new OnlinePlaceholderProvider()), MEMBERS(new MembersPlaceholderProvider()), LEVEL(new LevelPlaceholderProvider());

	private final IndividualTeamPlaceholderProvider teamProvider;
	private final IndividualTeamPlayerPlaceholderProvider teamPlayerProvider;

	/**
	 * Constructor to take in an interface per enum value
	 */
	private TeamPlaceholderOptionsEnum(IndividualTeamPlaceholderProvider teamProvider) {
		this.teamProvider = teamProvider;
		this.teamPlayerProvider = null;
	}

	/**
	 * Constructor to take in an interface per enum value
	 */
	private TeamPlaceholderOptionsEnum(IndividualTeamPlayerPlaceholderProvider teamPlayerProvider) {
		this.teamPlayerProvider = teamPlayerProvider;
		this.teamProvider = null;
	}

	public String applyPlaceholderProvider(Team team, TeamPlayer player) {
		if (teamProvider != null) {
			return teamProvider.getPlaceholderForTeam(team);
		}
		if (teamPlayerProvider != null && player != null) {
			return teamPlayerProvider.getPlaceholderForTeamPlayer(team, player);
		}

		return null;
	}

	public static TeamPlaceholderOptionsEnum getEnumValue(String str) {
		try {
			return TeamPlaceholderOptionsEnum.valueOf(str.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
