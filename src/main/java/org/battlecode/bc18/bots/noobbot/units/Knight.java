package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

import static org.battlecode.bc18.bots.util.Utils.gc;

public class Knight extends Robot {

    public static final UnitType TYPE = UnitType.Knight;

    /**
     * Constructor for Knight.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Knight(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Knight;
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

    @Override
    public void act() {
        //TODO: Implement this
    }

    @Override
    public UnitType getType() {
        return Knight.TYPE;
    }
}
