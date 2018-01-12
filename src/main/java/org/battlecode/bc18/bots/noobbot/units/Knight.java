package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public class Knight extends Robot {

    public static final UnitType TYPE = UnitType.Knight;

    Knight(int id) {
        super(id);
    }

    @Override
    public void act() {
        //TODO: Implement this
    }

    @Override
    public UnitType getType() {
        return Knight.TYPE;
    }
}
