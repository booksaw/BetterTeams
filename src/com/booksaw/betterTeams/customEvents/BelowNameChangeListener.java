package com.booksaw.betterTeams.customEvents;

/**
 * This class can be used if two plugins need access to the scoreboard.teams, so
 * when better teams adds/renvies a player to/from a new scoreboard your plugin
 * will be notified
 *
 * @author booksaw
 * @deprecated Use BelowNameChangeEvent like any other spigot event
 */
@Deprecated
public interface BelowNameChangeListener {

    /**
     * Run when a player is added to a new team or removed from a team
     *
     * @param event The event that has been added to a team or removed from a team
     */
    void run(BelowNameChangeEvent event);
}
