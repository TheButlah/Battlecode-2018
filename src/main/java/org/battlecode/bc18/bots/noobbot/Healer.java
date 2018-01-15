package org.battlecode.bc18.bots.noobbot;

import bc.Unit;
import org.battlecode.bc18.api.AbstractHealer;

public class Healer extends AbstractHealer {

    /**
     * Constructor for Healer.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Healer(Unit unit) {
        super(unit);
    }

    @Override
    public void act() {
        //TODO: Implement this
    }

    @Override
    protected void onDeath() {

    }
}
