package org.battlecode.bc18.api;

import static org.battlecode.bc18.util.Utils.gc;

import java.util.HashMap;
import java.util.Map;

import bc.Unit;
import bc.UnitType;

public abstract class AbstractFactory extends AbstractStructure {

    public static final UnitType TYPE = UnitType.Factory;

    @Override
    public UnitType getType() {
        return AbstractFactory.TYPE;
    }

    /**
     * Whether the factory can produce a robot.
     * Must not currently be producing a robot, and must have sufficient resources.
     * @param type The type of robot to check.
     */
    public boolean canProduceRobot(UnitType type) {
        return gc.canProduceRobot(getID(), type);
    }

    /**
     * Starts producing the robot of the given type.
     * NOTE: Does not check to see if it can produce it first.
     * @param type The UnitType of the robot to produce. Must be a robot.
     * @return The robot produced. Currently will always be null until fixed
     */
    public AbstractRobot produceRobot(UnitType type) {
        assert canProduceRobot(type);
        //println("Producing: " + type);
        gc.produceRobot(getID(), type);
        /*Unit unit = gc.unit(getMapLocation());
        return (AbstractStructure) AbstractUnit.makeUnit(unit);*/
        return null; //TODO: Figure out how to get the Unit object of a robot being built.
    }



    //////////END OF API//////////



    /**
     * Constructor for AbstractFactory.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AbstractFactory(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Factory;
    }
}
