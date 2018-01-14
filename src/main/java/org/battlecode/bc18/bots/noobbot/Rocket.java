package org.battlecode.bc18.bots.noobbot;

import bc.Unit;
import bc.UnitType;
import org.battlecode.bc18.api.AbstractRocket;
import org.battlecode.bc18.api.AbstractStructure;

public class Rocket extends AbstractRocket {

    /**
     * Constructor for AbstractRocket.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Rocket(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Rocket;
    }

    @Override
    public void act() {
        if (isDead()) return;

        //TODO: implement this
    }
}
