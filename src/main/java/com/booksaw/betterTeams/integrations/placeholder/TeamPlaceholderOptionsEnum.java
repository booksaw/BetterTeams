/**
 * 
 */
package com.booksaw.betterTeams.integrations.placeholder;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.integrations.placeholder.provider.*;

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
	ONLINE(new OnlinePlaceholderProvider()), MEMBERS(new MembersPlaceholderProvider()), DEFAULTMEMBERS(new DefaultMembersPlaceholderProvider()),
	ADMINS(new AdminsPlaceholderProvider()), OWNERS(new OwnersPlaceholderProvider()),
	POSITIONSCORE(new PositionScorePlaceholderProvider()), POSITIONBAL(new PositionBalPlaceholderProvider()),
	POSITIONMEMBERS(new PositionMembersPlaceholderProvider()), LEVEL(new LevelPlaceholderProvider()),
	MAXMONEY(new MaxMoneyPlaceholderProvider()), MAXMEMBERS(new MaxMembersPlaceholderProvider()), MAXWARPS(new MaxWarpsPlaceholderProvider()),
	PVP(new PvpPlaceholderProvider());

	private final IndividualTeamPlaceholderProvider teamProvider;
	private final IndividualTeamPlayerPlaceholderProvider teamPlayerProvider;

	/**
	 * Constructor to take in an interface per enum value
	 */
	TeamPlaceholderOptionsEnum(IndividualTeamPlaceholderProvider teamProvider) {
		this.teamProvider = teamProvider;
		this.teamPlayerProvider = null;
	}

	/**
	 * Constructor to take in an interface per enum value
	 */
	TeamPlaceholderOptionsEnum(IndividualTeamPlayerPlaceholderProvider teamPlayerProvider) {
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
