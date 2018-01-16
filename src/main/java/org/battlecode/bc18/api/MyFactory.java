package org.battlecode.bc18.api;

import bc.UnitType;

public interface MyFactory extends MyStructure {
    UnitType TYPE = UnitType.Factory;

    @Override
    default UnitType getType() {
        return TYPE;
    }

    /**
     * Whether the factory can produce a robot.
     * Must not currently be producing a robot, and must have sufficient resources.
     * @param type The type of robot to check.
     */
    boolean canProduceRobot(UnitType type);

    /**
     * Starts producing the robot of the given type.
     * NOTE: Does not check to see if it can produce it first.
     * @param type The UnitType of the robot to produce. Must be a robot.
     */
    void produceRobot(UnitType type);
}
