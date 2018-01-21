package org.battlecode.bc18.api;

import bc.Direction;
import bc.MapLocation;

public interface MyRobot extends MyUnit {
    /**
     * Checks to see if a direction is accessible, meaning there is nothing blocking physical access.
     * Takes into account only the map terrain, positions of other robots, and the edge of the game map.
     * NOTE: Does not take into account movement heat. Use `canMove()` or `isMoveReady()` for that.
     * @param dir The direction to check.
     * @return If there are no physical objects preventing movement to that direction.
     */
    boolean isAccessible(Direction dir);

    /** Whether the robot is ready to move. */
    boolean isMoveReady();

    /**
     * Checks to see if a direction is able to be moved to.
     * This means there is nothing blocking physical access and the movement is off cooldown.
     * Takes into account the map terrain, positions of other robots, edge of the game map, and movement heat.
     * @param dir The direction to check.
     * @return If there is nothing preventing movement to that direction.
     */
    boolean canMove(Direction dir);

    /**
     * Moves the robot in a direction.
     * NOTE: Does not check to see if it can move first.
     * @param dir The direction to move in.
     * @return The new location on the map that the robot moved to.
     */
    MapLocation move(Direction dir);

    /**
     * Tries to move the robot in the given direction. If it is not possible, then alternate nearby
     * directions are considered.
     * @param dir The direction to move in.
     * @return The new location on the map that the robot moved to.
     */
    MapLocation fuzzyMove(Direction dir);

    /** Gets the current active ability heat for this robot. */
    int getAbilityHeat();

    /** Gets the cooldown of this robot's active ability, i.e. how much heat it produces. */
    int getAbilityCooldown();

    /** Whether this unit has its active ability unlocked .*/
    boolean isAbilityUnlocked();

    /** Gets the range of this unit's active ability */
    int getAbililtyRange();
}
