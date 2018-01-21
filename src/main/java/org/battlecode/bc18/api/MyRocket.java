package org.battlecode.bc18.api;

import bc.MapLocation;
import bc.UnitType;

public interface MyRocket extends MyStructure {
    UnitType TYPE = UnitType.Rocket;

    public boolean canLaunchRocket(MapLocation destination);

    public void launchRocket(MapLocation destination);

    @Override
    default UnitType getType() {
        return TYPE;
    }
}
