package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;

import java.text.DecimalFormat;

public class MoneyComponent extends DoubleTeamComponent {

	public static String getFormattedDouble(double amount) {
		DecimalFormat df = new DecimalFormat("0.00");
		df.setGroupingUsed(true);
		df.setGroupingSize(3);
		return df.format(amount);
	}

	@Override
	public StoredTeamValue getSectionHeading() {
		return StoredTeamValue.MONEY;
	}

	public String getStringFormatting() {
		return getFormattedDouble(get());
	}

}
