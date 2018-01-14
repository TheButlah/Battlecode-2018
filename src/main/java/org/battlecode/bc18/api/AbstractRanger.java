package org.battlecode.bc18.api;

import bc.*;

public class AbstractRanger extends AbstractRobot {

    public static final UnitType TYPE = UnitType.Ranger;

    /**
     * Constructor for AbstractRanger.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    AbstractRanger(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Ranger;
    }

    /**
     * Begins the countdown to snipe a given location.
     * Maximizes the units attack and movement heats until the ranger has sniped.
     * The ranger may begin the countdown at any time,
     * including resetting the countdown to snipe a different location.
     * @param loc the MapLocation to begin snipping
     * @return true if snipping was successful, false otherwise
     */
    /*public boolean beginSnipe(MapLocation loc) {
        if (gc.isBeginSnipeReady(this.id) &&
                gc.canBeginSnipe(this.id, loc)) {
            gc.beginSnipe(this.id, loc);
            return true;
        }
        return false;
    }*/

    @Override
    public void act() {
        if (isDead()) return;
        //TODO: implement this
    }

    @Override
    public UnitType getType() {
        return AbstractRanger.TYPE;
    }
}
