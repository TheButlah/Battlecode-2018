package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

public abstract class Robot extends MyUnit {

    Robot(int id) {
        super(id);
    }

    public MapLocation moveTo(MapLocation loc) {
        return null;
    }
}
