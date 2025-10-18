package com.booksaw.betterTeams.util;

import com.booksaw.betterTeams.Main;

import java.text.DecimalFormat;

/**
 * Utility methods revolving around formatting money
 */
public class MoneyUtils {

	/**
	 * @return If short money is enabled in the config
	 */
	public static boolean useShortFormatting() {
		return Main.plugin.getConfig().getBoolean("useShortMoney");
	}

	/**
	 * @return The double formatted string, with the number of decimal places defined in the config
	 */
	public static String getDecimalPlaceFormattingString() {
		String doubleFormat = "0";
		int moneyDecimalPlaces = Main.plugin.getConfig().getInt("moneyDecimalPlaces");
		if (moneyDecimalPlaces > 0) {
			doubleFormat += ".";
			for (int i = 0; i < moneyDecimalPlaces; i++) {
				doubleFormat += "0";
			}
		}
		return doubleFormat;
	}

	/**
	 * Get a formatted double using the default formatting in the config
	 *
	 * @param amount The value of the money to format
	 * @return the formatted String
	 */
	public static String getFormattedDouble(double amount) {
		return getFormattedDouble(amount, getDecimalPlaceFormattingString(), useShortFormatting());
	}

	/**
	 * Get a formatted string from a monetary amount
	 *
	 * @param amount          the amount of money
	 * @param doubleFormat    the double format i.e. 0.00, used if shortFormatting is false
	 * @param shortFormatting if true will truncate the value, such as 12.0k
	 * @return a formatted string of the amount
	 */
	public static String getFormattedDouble(double amount, String doubleFormat, boolean shortFormatting) {
		if (shortFormatting) {
			return getFormattedShortDouble(amount);
		} else {
			DecimalFormat df = new DecimalFormat(doubleFormat);
			df.setGroupingUsed(true);
			df.setGroupingSize(3);
			return df.format(amount);
		}
	}

	/**
	 * Get the short formatted version of the amount, i.e. 10.0K
	 *
	 * @param amount The amount to formatted
	 * @return The created formatted string
	 */
	public static String getFormattedShortDouble(double amount) {
		if (amount >= 1_000_000_000) {
			return String.format("%.1fB", amount / 1_000_000_000);
		} else if (amount >= 1_000_000) {
			return String.format("%.1fM", amount / 1_000_000);
		} else if (amount >= 1_000) {
			return String.format("%.1fk", amount / 1_000);
		} else {
			return String.format("%.0f", amount);
		}
	}
}
