package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;
import com.booksaw.betterTeams.util.MoneyUtils;

public class MoneyComponent extends DoubleTeamComponent {

	@Override
	public StoredTeamValue getSectionHeading() {
		return StoredTeamValue.MONEY;
	}

	public String getStringFormatting() {
		return MoneyUtils.getFormattedDouble(get());
	}

	public String getMoneyShortFormatted() {
		return MoneyUtils.getFormattedShortDouble(get());
	}

}
