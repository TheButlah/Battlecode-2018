package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public class Factory extends Structure {

    public static final UnitType TYPE = UnitType.Factory;

    /**
     * Constructor for Factory.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
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
