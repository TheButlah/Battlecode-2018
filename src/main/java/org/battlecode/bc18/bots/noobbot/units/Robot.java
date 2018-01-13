package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

import static org.battlecode.bc18.bots.util.Utils.gc;

public abstract class Robot extends MyUnit {

    /**
     * Constructor for Robot.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Robot(Unit unit) {
        super(unit);
    }

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
}
