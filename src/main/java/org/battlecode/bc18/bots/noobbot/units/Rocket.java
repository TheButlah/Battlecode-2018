package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public class Rocket extends Structure {

    public static final UnitType TYPE = UnitType.Rocket;

    /**
     * Constructor for Rocket.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Rocket(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Rocket;
    }

    @Override
    public void act() {
        if (isDead()) return;

        //TODO: implement this
    }

    @Override
    public UnitType getType() {
        return Rocket.TYPE;
    }
}
