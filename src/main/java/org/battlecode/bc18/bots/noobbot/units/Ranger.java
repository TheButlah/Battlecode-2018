package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public class Ranger extends Robot {

    public static final UnitType TYPE = UnitType.Ranger;

    Ranger(int id) {
        super(id);
    }

    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    public UnitType getType() {
        return Ranger.TYPE;
    }
}
