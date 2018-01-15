package org.battlecode.bc18.api;

import bc.UnitType;

public interface MyRanger extends MyRobot, MyAttacker {
    UnitType TYPE = UnitType.Ranger;

    @Override
    default UnitType getType() {
        return TYPE;
    }

    /** The range within which the ranger cannot attack (inclusive) */
    int getCannotAttackRange();
}
