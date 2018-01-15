package org.battlecode.bc18.bots.noobbot;

import bc.Unit;
import bc.UnitType;
import org.battlecode.bc18.api.AbstractRanger;
import org.battlecode.bc18.api.AbstractRobot;

public class Ranger extends AbstractRanger {

    /**
     * Constructor for AbstractRanger.
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
