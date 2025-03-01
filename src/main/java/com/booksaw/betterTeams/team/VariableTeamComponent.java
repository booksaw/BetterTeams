package com.booksaw.betterTeams.team;

public interface VariableTeamComponent<T> {

	/**
	 * Add the given amount to this variable
	 *
	 * @param amount The amount to add
	 */
	void add(T amount);

	/**
	 * Subtract the given amount from this variable
	 *
	 * @param amount The amount to subtract
	 */
	void sub(T amount);

	/**
	 * Multiply this variable by the given amount
	 *
	 * @param amount The scaler
	 */
	void mult(T amount);

	/**
	 * Divide this variable by the given amount
	 *
	 * @param amount The scalar
	 */
	void div(T amount);

}
