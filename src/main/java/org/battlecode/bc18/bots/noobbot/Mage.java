package org.battlecode.bc18.bots.noobbot;

import bc.*;
import static org.battlecode.bc18.Utils.gc;

public class Mage extends Bot{

    public static final UnitType TYPE = UnitType.Mage;

    public Mage(int id) {
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
