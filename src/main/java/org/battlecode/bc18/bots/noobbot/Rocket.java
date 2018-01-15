package org.battlecode.bc18.bots.noobbot;

import bc.Unit;
import bc.UnitType;
import org.battlecode.bc18.api.ARocket;

public class Rocket extends ARocket {

    /**
     * Constructor for Rocket.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Rocket(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Rocket;
    }

    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    protected void onDeath() {

    }
}
