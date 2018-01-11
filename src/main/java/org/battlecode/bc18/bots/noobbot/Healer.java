package org.battlecode.bc18.bots.noobbot;

import bc.*;
import static org.battlecode.bc18.Utils.gc;

public class Healer extends Bot {

    public static final UnitType TYPE = UnitType.Healer;

    public Healer(int id) {
        super(id);
    }

    @Override
    public void act() {
        //TODO: Implement this
    }

    @Override
    public UnitType getType() {
        return Healer.TYPE;
    }
}
