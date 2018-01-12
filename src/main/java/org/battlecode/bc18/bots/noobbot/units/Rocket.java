package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public class Rocket extends Structure {

    public static final UnitType TYPE = UnitType.Rocket;

    Rocket(int id) {
        super(id);
    }

    @Override
    public void act() {
        //TODO: implement this
    }

    @Override
    public UnitType getType() {
        return Rocket.TYPE;
    }
}
