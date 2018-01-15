package org.battlecode.bc18.api;

import bc.Unit;
import bc.UnitType;

public interface MyKnight extends MyRobot, MyAttacker{
    UnitType TYPE = UnitType.Knight;

    @Override
    default UnitType getType() {
        return TYPE;
    }

    /**
     * Whether the javelin is ready to use.
     * NOTE: Checks both heat and unlock status.
     */
    boolean isJavelinReady();

    /** Whether the target is within javelin range */
    boolean isWithinJavelinRange(Unit target);

    /**
     * Whether we can javelin the target.
     * Checks heat, distance, and unlock status.
     */
    boolean canJavelin(Unit target);

    /**
     * Javelins the target, dealing the knight's standard damage.
     * NOTE: Does not check to see if we can first.
     * @param target The target unit.
     */
    void javelin(Unit target);
}
