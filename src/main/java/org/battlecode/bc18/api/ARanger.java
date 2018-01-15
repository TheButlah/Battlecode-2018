package org.battlecode.bc18.api;

import bc.*;
import static org.battlecode.bc18.util.Utils.gc;

public abstract class ARanger extends ARobot implements MyRanger {

    @Override
    public int getCannotAttackRange() {
        return cannotAttackRange;
    }

    @Override
    public int getAttackRange() {
        return (int) getAsUnit().attackRange();
        //TODO: Use gc.researchInfo() to do this instead
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



    //////////END OF API//////////



    private final int cannotAttackRange;

    /**
     * Constructor for ARanger.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected ARanger(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Ranger;
        cannotAttackRange = (int) getAsUnit().rangerCannotAttackRange();
    }
}
