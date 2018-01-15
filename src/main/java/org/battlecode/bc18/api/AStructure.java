package org.battlecode.bc18.api;

import bc.Direction;
import bc.MapLocation;
import bc.Unit;
import org.battlecode.bc18.util.Utils;

import java.util.List;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AStructure extends AUnit implements MyStructure {

    @Override
    public boolean isBuilt() {
        return Utils.toBool(getAsUnit().structureIsBuilt());
        //TODO: Cache/calculate this
    }

    @Override
    public List<MyRobot> getGarrison() {

        @SuppressWarnings("unchecked")
        //TODO: Can we determine whether this will cast will give an Exception?
        List<MyRobot> result = (List<MyRobot>) getUnits(getAsUnit().structureGarrison());
        return result;
    }

    @Override
    public boolean canUnload(Direction direction) {
        return gc.canUnload(getID(), direction);
    }

    @Override
    public MyRobot unload(Direction direction) {
        assert canUnload(direction);
        gc.unload(getID(), direction);
        MapLocation unloadLoc = getMapLocation().add(direction);
        ARobot unloadedUnit = (ARobot) getUnit(gc.senseUnitAtLocation(unloadLoc));
        unloadedUnit.setLocation(unloadLoc); //Ensure that the internal state matches reality
        return unloadedUnit;
    }

    //////////END OF API//////////

    /**
     * Constructor for AStructure.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    AStructure(Unit unit) {
        super(unit);
    }

}
