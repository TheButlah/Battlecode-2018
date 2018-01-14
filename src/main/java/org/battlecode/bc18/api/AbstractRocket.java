package org.battlecode.bc18.api;

import bc.*;

public class AbstractRocket extends AbstractStructure {

    public static final UnitType TYPE = UnitType.Rocket;

    /**
     * Constructor for AbstractRocket.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    AbstractRocket(Unit unit) {
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
        return AbstractRocket.TYPE;
    }
}
