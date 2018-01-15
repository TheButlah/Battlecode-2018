package org.battlecode.bc18.api;

import bc.*;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AbstractKnight extends AbstractRobot {

    public static final UnitType TYPE = UnitType.Knight;

    @Override
    public UnitType getType() {
        return AbstractKnight.TYPE;
    }

    /**
     * Javelins the robot, dealing the knight's standard damage.
     * @param targetID target unit id
     * @return true if javelin was successful, false otherwise
     */
    public boolean javelin(int targetID) {
        if (gc.isJavelinReady(getID()) &&
            gc.canJavelin(getID(), targetID)) {
            gc.javelin(getID(), targetID);
            return true;
        }
        return false;
    }



    //////////END OF API//////////



    /**
     * Constructor for AbstractKnight.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AbstractKnight(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Knight;
    }
}
