package org.battlecode.bc18.api;

import bc.Direction;
import bc.MapLocation;
import bc.Unit;
import org.battlecode.bc18.util.Utils;

import java.util.List;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AbstractStructure extends AbstractUnit {

    /** Whether the structure is fully built or not. */
    public boolean isBuilt() {
        return Utils.toBool(getAsUnit().structureIsBuilt());
        //TODO: Cache/calculate this
    }

    /** Gets the garrison of this Structure. */
    public List<AbstractRobot> getGarrison() {

        @SuppressWarnings("unchecked")
        //TODO: Can we determine if this will give an Exception?
        List<AbstractRobot> result = (List<AbstractRobot>) getUnits(getAsUnit().structureGarrison());
        return result;
    }

    /**
     * Whether the factory can unload one of its garrisoned robots in a given direction.
     * There must be space in that direction, and the unit must be ready to move.
     * @param direction The direction to check.
     */
    public boolean canUnload(Direction direction) {
        return gc.canUnload(getID(), direction);
    }

    /**
     * Unloads a garrisoned robot in a given direction.
     * NOTE: Does not check to see if it can first.
     * @param direction The direction to unload to.
     * @return The robot that was unloaded.
     */
    public AbstractRobot unload(Direction direction) {
        assert canUnload(direction);
        gc.unload(getID(), direction);
        MapLocation unloadLoc = getMapLocation().add(direction);
        AbstractRobot unloadedUnit = (AbstractRobot) getUnit(gc.senseUnitAtLocation(unloadLoc));
        unloadedUnit.setLocation(unloadLoc); //Ensure that the internal state matches reality
        return unloadedUnit;
    }

    //////////END OF API//////////

    /**
     * Constructor for AbstractStructure.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AbstractStructure(Unit unit) {
        super(unit);
    }

}
