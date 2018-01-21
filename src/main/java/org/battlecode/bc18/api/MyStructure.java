package org.battlecode.bc18.api;

import bc.Direction;

import java.util.List;

public interface MyStructure extends MyUnit {
    /** Whether the structure is fully built or not. */
    boolean isBuilt();

    /** Gets the garrison of this Structure. */
    List<MyRobot> getGarrison();

    /**
     * Whether the factory can unload one of its garrisoned robots in a given direction.
     * There must be space in that direction, and the unit must be ready to move.
     * @param direction The direction to check.
     */
    boolean canUnload(Direction direction);

    /**
     * Unloads a garrisoned robot in a given direction.
     * NOTE: Does not check to see if it can first.
     * @param direction The direction to unload to.
     * @return The robot that was unloaded.
     */
    MyRobot unload(Direction direction);

    /**
     * @param robot
     * @return Whether the given robot can be loaded into this structure's garrison
     */
    boolean canLoad(MyRobot robot);

    /**
     * Loads a robot into this structure's garrison
     * @param robot The robot to load
     */
    void load(MyRobot robot);
}
