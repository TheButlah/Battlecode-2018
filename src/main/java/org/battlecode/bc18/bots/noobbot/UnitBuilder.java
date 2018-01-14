package org.battlecode.bc18.bots.noobbot;

import bc.Unit;
import bc.UnitType;
import org.battlecode.bc18.api.*;

public class UnitBuilder implements org.battlecode.bc18.api.UnitBuilder {
    @Override
    public AbstractUnit newUnit(Unit ourUnit) {
        UnitType type = ourUnit.unitType();
        switch(type) {
            case Worker:
                return new Worker(ourUnit);
            case Knight:
                return new Knight(ourUnit);
            case Ranger:
                return new Ranger(ourUnit);
            case Mage:
                return new Mage(ourUnit);
            case Healer:
                return new Healer(ourUnit);
            case Factory:
                return new Factory(ourUnit);
            case Rocket:
                return new Rocket(ourUnit);
            default:
                throw new RuntimeException("Unrecognized UnitType: " + type); //Should never happen
        }    }
}
