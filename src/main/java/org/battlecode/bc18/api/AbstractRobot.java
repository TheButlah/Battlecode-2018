package org.battlecode.bc18.api;

import bc.*;
import org.battlecode.bc18.util.Utils;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AbstractRobot extends AUnit {

    /**
     * Checks to see if a direction is accessible, meaning there is nothing blocking physical access.
     * Takes into account only the map terrain, positions of other robots, and the edge of the game map.
     * NOTE: Does not take into account movement heat. Use `canMove()` or `isMoveReady()` for that.
     * @param dir The direction to check.
     * @return If there are no physical objects preventing movement to that direction.
     */
    public boolean isAccessible(Direction dir) {
        return gc.canMove(getID(), dir);
    }

    /** Whether the robot is ready to move. */
    public boolean isMoveReady() {
        return gc.isMoveReady(getID());
    }

    /**
     * Checks to see if a direction is able to be moved to.
     * This means there is nothing blocking physical access and the movement is off cooldown.
     * Takes into account the map terrain, positions of other robots, edge of the game map, and movement heat.
     * @param dir The direction to check.
     * @return If there is nothing preventing movement to that direction.
     */
    public boolean canMove(Direction dir) {
        return isAccessible(dir) && isMoveReady();
    }

    /**
     * Moves the robot in a direction.
     * NOTE: Does not check to see if it can move first.
     * @param dir The direction to move in.
     * @return The new location on the map that the robot moved to.
     */
    public MapLocation move(Direction dir) {
        assert canMove(dir);
        gc.moveRobot(getID(), dir);
        MapLocation newLoc = getMapLocation().add(dir);
        setLocation(newLoc);
        return newLoc;
    }


    /** Gets the current active ability heat for this robot. */
    public int getAbilityHeat() {
        return (int) getAsUnit().abilityHeat();
    }

    /** Gets the cooldown of this robot's active ability, i.e. how much heat it produces. */
    public int getAbilityCooldown() {
        if (abilityCooldown == -1) abilityCooldown = (int) getAsUnit().abilityCooldown();
        return abilityCooldown;
    }

    /** Whether this unit has its active ability unlocked .*/
    public boolean isAbilityUnlocked() {
        return Utils.toBool(getAsUnit().isAbilityUnlocked());
    }

    /** Gets the range of this unit's active ability */
    public int getAbililtyRange() {
        if (abilityRange == -1) {
            long tmp = getAsUnit().abilityRange();
            if (tmp > Integer.MAX_VALUE) tmp = Integer.MAX_VALUE; //prevent overflow from snipe ability
            abilityRange = (int) tmp;
        }
        return abilityRange;
    }



    //////////END OF API//////////



    //We don't know these values yet
    private int abilityCooldown = -1;
    private int abilityRange = -1;

    /**
     * Constructor for AbstractRobot.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    AbstractRobot(Unit unit) {
        super(unit);
    }
}
