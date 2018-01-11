package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.Utils;

import static org.battlecode.bc18.Utils.gc;

public class Factory extends Bot {

    public static final UnitType TYPE = UnitType.Factory;

    public Factory(int id) {
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
