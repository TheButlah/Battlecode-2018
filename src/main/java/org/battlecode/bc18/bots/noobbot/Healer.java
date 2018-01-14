package org.battlecode.bc18.bots.noobbot;

import bc.Unit;
import bc.UnitType;
import org.battlecode.bc18.api.AbstractHealer;
import org.battlecode.bc18.api.AbstractRobot;

import static org.battlecode.bc18.bots.util.Utils.gc;

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
        if (isDead()) return;
        //TODO: Implement this
    }
}
