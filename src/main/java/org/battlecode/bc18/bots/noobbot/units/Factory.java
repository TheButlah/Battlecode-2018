package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public class Factory extends Structure {

    public static final UnitType TYPE = UnitType.Factory;

    /**
     * Constructor for Factory.
     * @exception RuntimeException Occurs when a unit with that id already exists.
     */
    Factory(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Factory;
    }

    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    public UnitType getType() {
        return Factory.TYPE;
    }
}
