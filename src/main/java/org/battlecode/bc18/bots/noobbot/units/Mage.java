package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

import static org.battlecode.bc18.Utils.gc;

public class Mage extends Robot {

    public static final UnitType TYPE = UnitType.Mage;

    /**
     * Constructor for MyUnit.
     * @exception RuntimeException Occurs when a unit with that id already exists.
     */
    Mage(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Mage;
    }

    /**
     * Blinks the mage to the given location.
     * @param loc the MapLocation to blink
     * @return true if blinking was successful, false otherwise
     */
    public boolean blink(MapLocation loc) {
        if (gc.isBlinkReady(this.id) &&
                gc.canBlink(this.id, loc)) {
            gc.blink(this.id, loc);
            return true;
        }
        return false;
    }

    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    public UnitType getType() {
        return Mage.TYPE;
    }
}
