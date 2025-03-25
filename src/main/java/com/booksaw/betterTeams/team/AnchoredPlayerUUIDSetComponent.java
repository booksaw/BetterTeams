package com.booksaw.betterTeams.team;
import java.util.UUID;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;

public class AnchoredPlayerUuidSetComponent extends UuidSetComponent {

    /**
     * The result of using the add and remove methods of this enum's enclosing class
     */
    public enum AnchorResult {
        /**
         * The operation was successful
         */ 
        SUCCESS,
        /**
         * The player is not in the team
         */
        NOT_IN_TEAM,
        /**
         * The player is already anchored
         */
        ALREADY_ANCHORED,
        /**
         * The player is not anchored
         */
        NOT_ANCHORED
    }

    /**
     * Recommended to use this add over the void add,
     * as it does the corresponding checks
     * @param team The team that the player must be in for this to be successful
     * @param player The team player
     * @return AnchorResult
     */
    public AnchorResult add(Team team, TeamPlayer player) {
        if (!team.getMembers().getClone().contains(player))
            return AnchorResult.NOT_IN_TEAM;
        else if(team.isPlayerAnchored(player)) {
            return AnchorResult.ALREADY_ANCHORED;
        }
        player.setAnchor(true);
        team.getStorage().setAnchor(player, true);
        add(team, player.getPlayerUUID());
        return AnchorResult.SUCCESS;
    }

    @Override
    public void add(Team team, UUID playerUUID) {
        set.add(playerUUID);
    }

    /**
     * Recommended to use this remove over the void remove,
     * as it does the corresponding checks
     * @param team The team that the player must be in for this to be successful
     * @param player The team player
     * @return AnchorResult
     */
    public AnchorResult remove(Team team, TeamPlayer player) {
        if (!team.getMembers().getClone().contains(player))
            return AnchorResult.NOT_IN_TEAM;
        else if (!team.isPlayerAnchored(player)) {
            return AnchorResult.NOT_ANCHORED;
        }
        player.setAnchor(false);
        team.getStorage().setAnchor(player, false);
        remove(team, player.getPlayerUUID());
        return AnchorResult.SUCCESS;
    }

    @Override
    public void remove(Team team, UUID playerUUID) {
        set.remove(playerUUID);
    }

    @Override
    public String getSectionHeading() {
        return "anchoredPlayers";
    }

    @Override
    public void load(TeamStorage section) {
        set.clear();
        set.addAll(section.getAnchoredPlayerList());
    }

    @Override
    public void save(TeamStorage storage) {
        storage.setAnchoredPlayerList(getConvertedList());
    }
}
