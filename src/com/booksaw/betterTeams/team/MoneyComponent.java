package com.booksaw.betterTeams.team;

import java.text.DecimalFormat;

public class MoneyComponent extends DoubleTeamComponent {

	@Override
	public String getSectionHeading() {
		return "money";
	}

	public String getStringFormatting() {
		DecimalFormat df = new DecimalFormat("0.00");
		df.setGroupingUsed(true);
		df.setGroupingSize(3);
		return df.format(get());
	}

}
