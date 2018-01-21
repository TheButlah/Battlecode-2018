package org.battlecode.bc18.api;

import java.util.List;

import org.battlecode.bc18.util.Utils;

import bc.MapLocation;
import bc.Unit;
import bc.UnitType;

public abstract class ARocket extends AStructure implements MyRocket {

    @Override
    public boolean canLaunchRocket(MapLocation destination) {
        return Utils.gc.canLaunchRocket(getID(), destination);
    }

    @Override
    public void launchRocket(MapLocation destination) {
        assert canLaunchRocket(destination);
        Utils.gc.launchRocket(getID(), destination);
        List<MyRobot> garrisonRobots = getGarrison();
        for (MyRobot robot : garrisonRobots) {
            ((AUnit)robot).informOfDeath(); // Units launched into space are now considered to be dead
        }
        informOfDeath();
    }

    //////////END OF API//////////

    /**
     * Constructor for ARocket.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected ARocket(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Rocket;
    }
}
