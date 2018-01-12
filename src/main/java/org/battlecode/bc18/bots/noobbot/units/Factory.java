package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public class Factory extends Structure {

    public static final UnitType TYPE = UnitType.Factory;

    Factory(int id) {
        super(id);
    }

    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    public UnitType getType() {
        return Factory.TYPE;
    }
}
