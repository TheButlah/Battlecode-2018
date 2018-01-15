package org.battlecode.bc18.bots.noobbot;

import bc.Unit;
import bc.UnitType;
import org.battlecode.bc18.api.AbstractMage;

public class Mage extends AbstractMage {

    /**
     * Constructor for AbstractMage.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Mage(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Mage;
    }


    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    protected void onDeath() {

    }
}
