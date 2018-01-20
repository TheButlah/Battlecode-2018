package org.battlecode.bc18.api;

import bc.Unit;
import bc.UnitType;

public interface MyRanger extends MyRobot, MyAttacker {
    UnitType TYPE = UnitType.Ranger;

    @Override
    default UnitType getType() {
        return TYPE;
    }

    /** The range within which the ranger cannot attack (inclusive) */
    int getCannotAttackRange();

    int getAttackRingSize();

    boolean isSnipeReady();

    boolean canSnipe(Unit target);

    boolean canAct();

    boolean snipe(Unit target);

    boolean isTargetKindaClose(Unit target);

    boolean isTargetKindaFar(Unit target);

    boolean isTargetInSweetSpot(Unit target);
}
