package org.battlecode.bc18.bots.noobbot.units;

import bc.Unit;
import bc.UnitType;

public abstract class Structure extends MyUnit {

    /**
     * Constructor for MyUnit.
     * @exception RuntimeException Occurs when a unit with that id already exists.
     */
    protected Structure(Unit unit) {
        super(unit);
    }

}
