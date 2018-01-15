package org.battlecode.bc18.bots.noobbot;

import bc.Unit;
import bc.UnitType;
import org.battlecode.bc18.api.ARanger;

public class Ranger extends ARanger {

    /**
     * Constructor for Ranger.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Ranger(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Ranger;
    }


    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    protected void onDeath() {

    }
}
