package org.battlecode.bc18.bots.noobbot.units;

import bc.*;
import org.battlecode.bc18.Utils;
import org.battlecode.bc18.bots.noobbot.Main;

import static org.battlecode.bc18.Utils.gc;

public class Knight extends Robot {

    public static final UnitType TYPE = UnitType.Knight;

    /**
     * Constructor for MyUnit.
     * @exception RuntimeException Occurs when a unit with that id already exists.
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
        if (gc.isJavelinReady(this.id) &&
                gc.canJavelin(this.id, targetID)) {
            gc.javelin(this.id, targetID);
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
