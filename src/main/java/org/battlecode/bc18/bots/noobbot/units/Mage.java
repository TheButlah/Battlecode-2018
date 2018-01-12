package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public class Mage extends Robot {

    public static final UnitType TYPE = UnitType.Mage;

    Mage(int id) {
        super(id);
    }

    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    public UnitType getType() {
        return Mage.TYPE;
    }
}
