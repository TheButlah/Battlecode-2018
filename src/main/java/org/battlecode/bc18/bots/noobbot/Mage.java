package org.battlecode.bc18.bots.noobbot;

import bc.Unit;

public class Mage extends Bot{

    public Mage(Unit unit) {
        super(unit);
    }

    @Override
    public void act() {
        System.out.printf("Bot %d of type %s is acting!", ID, TYPE.toString());
    }
}
