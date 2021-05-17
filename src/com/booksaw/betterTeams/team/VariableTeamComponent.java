package com.booksaw.betterTeams.team;

public interface VariableTeamComponent<T> {

	/**
	 * Add the given amount to this variable
	 * 
	 * @param amount The amount to add
	 */
	public void add(T amount);

	/**
	 * Subtract the given amount from this variable
	 * 
	 * @param amount The amount to subtract
	 */
	public void sub(T amount);

	/**
	 * Multiply this variable by the given amount
	 * 
	 * @param amount The scaler
	 */
	public void mult(T amount);

	/**
	 * Divide this variable by the given amount
	 * 
	 * @param amount The scalar
	 */
	public void div(T amount);

}
