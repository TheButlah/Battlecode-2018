package org.battlecode.bc18.api;

import bc.UnitType;

public interface MyRocket extends MyStructure {
    UnitType TYPE = UnitType.Rocket;

    @Override
    default UnitType getType() {
        return TYPE;
    }
}
