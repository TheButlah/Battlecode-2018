package org.battlecode.bc18.api;

import static org.battlecode.bc18.util.Utils.gc;

import bc.Unit;
import bc.UnitType;

public abstract class AFactory extends AStructure implements MyFactory {

    @Override
    public boolean canProduceRobot(UnitType type) {
        return gc.canProduceRobot(getID(), type);
    }

    @Override
    public MyRobot produceRobot(UnitType type) {
        assert canProduceRobot(type);
        gc.produceRobot(getID(), type);
        System.out.println("Garrison after production: ");
        getGarrison().forEach(System.out::print);
        //return (MyStructure) MyUnit.makeUnit(unit);*/
        return null; //TODO: Figure out how to get the MyUnit object of a robot being built.
    }



    //////////END OF API//////////



    /**
     * Constructor for AFactory.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AFactory(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Factory;
    }
}
