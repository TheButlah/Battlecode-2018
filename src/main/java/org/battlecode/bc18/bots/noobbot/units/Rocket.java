package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public class Rocket extends Structure {

    public static final UnitType TYPE = UnitType.Rocket;

    /**
     * Constructor for MyUnit.
     * @exception RuntimeException Occurs when a unit with that id already exists.
     */
    Rocket(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Rocket;
    }

    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    public UnitType getType() {
        return Rocket.TYPE;
    }
}
